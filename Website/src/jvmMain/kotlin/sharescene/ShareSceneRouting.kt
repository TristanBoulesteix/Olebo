package fr.olebo.sharescene

import fr.olebo.sharescene.html.shareSceneUi
import fr.olebo.synchronizedSessionSet
import io.ktor.application.*
import io.ktor.html.*
import io.ktor.http.cio.websocket.*
import io.ktor.routing.*
import io.ktor.websocket.*
import kotlinx.html.HTML
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

private const val SESSION_CODE_PARAM = "sessionCode"

fun Routing.shareSceneRouting() {
    val shareSceneSessions = synchronizedSessionSet()

    get("share-scene/{?}") {
        call.respondHtml(block = HTML::shareSceneUi)
    }

    // Olebo desktop app (Sender)
    webSocket("share-scene") {
        val currentConnection = Connection()

        val currentSession = ShareSceneSession(currentConnection).also { shareSceneSessions += it }

        send(NewSessionCreated(currentSession.sessionId, currentSession.urlCode))

        for (frame in incoming) {
            when (frame) {
                is Frame.Text -> {
                    val message = frame.getMessageOrNull()

                    when (message) {
                        is NewMap -> {
                            currentSession.map = Map(message.backgroundImage, message.tokens)
                        }
                        else -> continue
                    }

                    currentSession.sendToPlayers(message)
                }
                else -> continue
            }
        }

        shareSceneSessions destroy currentSession
    }

    webSocket("share-scene/{$SESSION_CODE_PARAM}") {
        val currentSession =
            shareSceneSessions.find { it.urlCode == call.parameters[SESSION_CODE_PARAM] } ?: return@webSocket

        val userName = call.request.queryParameters["name"] ?: return@webSocket

        val currentConnection = Connection()

        currentSession.playerConnections += currentConnection
        currentSession.sendToMaster(NumberOfConnectedUser(currentSession.playerConnections.size))

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
        currentSession.playerConnections -= currentConnection
        currentSession.sendToMaster(NumberOfConnectedUser(currentSession.playerConnections.size))
    }
}