package fr.olebo.sharescene

import fr.olebo.sharescene.html.shareSceneUi
import fr.olebo.synchronizedSessionSet
import io.ktor.application.*
import io.ktor.html.*
import io.ktor.http.cio.websocket.*
import io.ktor.routing.*
import io.ktor.websocket.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.html.HTML
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

private const val SESSION_CODE_PARAM = "sessionCode"
private const val MINIMAL_OLEBO_VERSION_COMPAT = 5

fun Routing.shareSceneRouting() {
    val shareSceneSessions = synchronizedSessionSet()
    val mutexSession = Mutex()

    get("share-scene/{?}") {
        call.respondHtml(block = HTML::shareSceneUi)
    }

    // Olebo desktop app (Sender)
    webSocket("share-scene") {
        val currentConnection = Connection()

        val currentSession = ShareSceneSession(currentConnection).also {
            mutexSession.withLock { shareSceneSessions += it }
        }

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
            shareSceneSessions.find { it.urlCode == call.parameters[SESSION_CODE_PARAM] } ?: kotlin.run {
                send(ConnectionRefused)
                return@webSocket
            }
        }

        val userName = call.request.queryParameters["name"] ?: return@webSocket

        val currentConnection = Connection(userName)

        currentSession.mutex.withLock { currentSession.playerConnections += currentConnection }
        currentSession.sendToMaster(PlayerAddedOrRemoved(currentSession.getPlayers()))

        val (background, tokens) = currentSession.map

        currentSession.sendToPlayers(NewMap(background, tokens))

        for (frame in incoming) {
            when (frame) {
                is Frame.Text -> {
                    when (Json.decodeFromString<Message>(frame.readText())) {
                        else -> continue
                    }
                }
                else -> continue
            }
        }

        // Handle session close
        currentSession.mutex.withLock { currentSession.playerConnections -= currentConnection }
        currentSession.sendToMaster(PlayerAddedOrRemoved(currentSession.getPlayers()))
    }
}