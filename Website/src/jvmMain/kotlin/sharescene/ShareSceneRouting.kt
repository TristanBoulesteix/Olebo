package fr.olebo.sharescene

import fr.olebo.sharescene.html.connectToShareScene
import fr.olebo.synchronizedSessionSet
import io.ktor.application.*
import io.ktor.html.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.websocket.*
import kotlinx.html.HTML

private const val SESSION_CODE_PARAM = "sessionCode"

fun Routing.shareSceneRouting() {
    val shareSceneSessions = synchronizedSessionSet()

    get("share-scene") {
        call.respondHtml(block = HTML::connectToShareScene)
    }

    // Olebo desktop app (Sender)
    webSocket("share-scene") {
        val currentConnection = Connection()

        val currentSession = ShareSceneSession(currentConnection).also { shareSceneSessions += it }

        send(NewSessionCreated(currentSession.sessionId, currentSession.urlCode))

        for (frame in incoming) {
            when (frame) {
                else -> Unit
            }
        }

        shareSceneSessions destroy currentSession
    }

    val urlClients = "share-scene/{$SESSION_CODE_PARAM}"

    // Web clients (Receivers)
    get(urlClients) {
        if(shareSceneSessions.any { it.urlCode == call.parameters[SESSION_CODE_PARAM] }) {
            call.respondText("Test")
        } else {
            call.respond(HttpStatusCode.BadGateway)
        }
    }

    webSocket(urlClients) {
        val currentSession =
            shareSceneSessions.find { it.urlCode == call.parameters[SESSION_CODE_PARAM] } ?: return@webSocket

        val currentConnection = Connection()

        currentSession.playerConnections += currentConnection

        for (frame in incoming) {
            when (frame) {
                is Frame.Text -> {
                    /*when (Json.decodeFromString<Message>(frame.readText())) {
                        else -> continue
                    }*/
                }
                else -> Unit
            }
        }

        // Handle session close
        currentSession.playerConnections -= currentConnection
    }
}