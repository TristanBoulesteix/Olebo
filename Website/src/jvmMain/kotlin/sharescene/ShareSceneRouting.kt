package fr.olebo.sharescene

import fr.olebo.synchronizedSessionSet
import io.ktor.http.cio.websocket.*
import io.ktor.routing.*
import io.ktor.websocket.*
import java.util.*

private const val ID_PARAM_NAME = "sceneId"

fun Routing.shareSceneRouting()  {
    val shareSceneSessions = synchronizedSessionSet()

    // Olebo desktop app (Sender)
    webSocket("share-scene") {
        val currentConnection = Connection()

        val currentSession = ShareSceneSession(masterConnection = currentConnection).also { shareSceneSessions += it }

        send(NewSessionCreated(currentSession.sessionId))

        for (frame in incoming) {
            when(frame) {
                else -> Unit
            }
        }

        shareSceneSessions destroy currentSession
    }

    // Web clients (Receivers)
    webSocket("share-scene/{$ID_PARAM_NAME}") {
        val sessionId: UUID = try {
            UUID.fromString(call.parameters[ID_PARAM_NAME])
        } catch (t: Throwable) {
            return@webSocket
        }

        val currentConnection = Connection()

        val currentSession = shareSceneSessions.find { it.sessionId == sessionId }?.also {
            it.playerConnections += currentConnection
        } ?: ShareSceneSession(masterConnection = currentConnection).also {
            shareSceneSessions += it
        }

        val isMasterConnection = currentConnection === currentSession.masterConnection

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
        if (isMasterConnection) {
            shareSceneSessions destroy currentSession
        } else {
            currentSession.playerConnections -= currentConnection
        }
    }
}