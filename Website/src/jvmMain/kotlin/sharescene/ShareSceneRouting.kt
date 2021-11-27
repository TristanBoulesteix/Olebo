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

        val currentConnection = Connection(this)

        val currentSession = shareSceneSessions.find { it.sessionId == sessionId }?.also {
            it.playerConnections += currentConnection
            it.masterConnection.send("New player joined with id: ${currentConnection.id}")
        } ?: ShareSceneSession(
            sessionId,
            currentConnection
        ).also { shareSceneSessions += it }

        for (frame in incoming) {
            when (frame) {
                is Frame.Text -> println(frame.readText())
                else -> Unit
            }
        }

        // Handle session close
        if (currentConnection === currentSession.masterConnection) {
            shareSceneSessions destroy currentSession
        } else {
            currentSession.playerConnections -= currentConnection
        }
    }
}