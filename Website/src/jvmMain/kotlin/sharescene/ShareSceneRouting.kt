package fr.olebo.sharescene

import fr.olebo.synchronizedSessionSet
import io.ktor.http.cio.websocket.*
import io.ktor.routing.*
import io.ktor.websocket.*

private const val ID_PARAM_NAME = "sceneId"

fun Routing.shareSceneRouting() {
    val shareSceneSessions = synchronizedSessionSet()

    webSocket("scene/{$ID_PARAM_NAME}") {
        val sessionId = call.parameters[ID_PARAM_NAME]!!

        val currentConnection = Connection()

        val currentSession = shareSceneSessions.find { it.sessionId == sessionId }?.also {
            it.playerConnections += currentConnection
        } ?: ShareSceneSession(
            sessionId,
            currentConnection
        ).also { shareSceneSessions += it }

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