package fr.olebo.sharescene

import fr.olebo.log.log
import fr.olebo.sharescene.html.shareSceneUi
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.html.HTML

private const val SESSION_CODE_PARAM = "sessionCode"
private const val MINIMAL_OLEBO_VERSION_COMPAT = 6

fun Routing.shareSceneRouting() {
    val shareSceneSessions = synchronizedSessionSet()
    val mutexSession = Mutex()

    get("share-scene/{?}") {
        call.respondHtml(block = HTML::shareSceneUi)
    }

    // Olebo desktop app (Sender)
    webSocket("share-scene") {
        val currentConnection = Connection()

        val currentSession = withTimeoutOrNull(3_000) {
            currentConnection.createSession()
        } ?: kotlin.run {
            close(CloseReason(CloseReason.Codes.TRY_AGAIN_LATER, "Session creation failed - Session timeout"))
            log.error("Timeout - Unable to create a new session.")
            return@webSocket
        }

        log.info("New session created (UUID=${currentSession.sessionId}).")

        mutexSession.withLock { shareSceneSessions += currentSession }

        send(NewSessionCreated(currentSession.sessionId, currentSession.urlCode, MINIMAL_OLEBO_VERSION_COMPAT))

        for (frame in incoming) {
            when (frame) {
                is Frame.Text -> {
                    val message = frame.getMessageOrNull()

                    when (message) {
                        is NewMap -> currentSession.map = Map(message.backgroundImage, message.tokens)
                        is TokenStateChanged -> currentSession.map = currentSession.map.copy(tokens = message.tokens)
                        is CursorHidden -> currentSession.cursor = null
                        is CursorMoved -> currentSession.cursor = message.cursor
                        else -> continue
                    }

                    currentSession.sendToPlayers(message)
                }
                else -> continue
            }
        }

        mutexSession.withLock { shareSceneSessions destroy currentSession }
    }

    webSocket("share-scene/{$SESSION_CODE_PARAM}") {
        val currentSession = mutexSession.withLock {
            val sessionCode = call.parameters[SESSION_CODE_PARAM]

            shareSceneSessions.find { it.urlCode == sessionCode } ?: kotlin.run {
                send(ConnectionRefused)
                log.trace("Connection refused for player (sessionCode=$sessionCode).")
                return@webSocket
            }
        }

        val userName = call.request.queryParameters["name"] ?: kotlin.run {
            send(ConnectionRefused)
            log.trace("Connection refused for player (Empty name).")
            return@webSocket
        }

        val currentConnection = Connection(userName)

        currentSession.mutex.withLock { currentSession.playerConnections += currentConnection }

        log.info("New player connected (SessionUUID=${currentSession.sessionId}).")

        currentSession.sendToMaster(PlayerAddedOrRemoved(currentSession.getPlayers()))

        val (background, tokens) = currentSession.map

        currentSession.sendToPlayers(NewMap(background, tokens))
        currentSession.cursor?.let { currentSession.sendToPlayers(CursorMoved(it)) }

        for (frame in incoming) {
            continue
        }

        // Handle session close
        currentSession.mutex.withLock { currentSession.playerConnections -= currentConnection }
        currentSession.sendToMaster(PlayerAddedOrRemoved(currentSession.getPlayers()))
    }
}