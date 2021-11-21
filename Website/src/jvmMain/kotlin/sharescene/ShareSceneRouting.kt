package fr.olebo.sharescene

import fr.olebo.synchronizedConnectionsSet
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
            currentConnection,
            synchronizedConnectionsSet()
        ).also { shareSceneSessions += it }

        for (frame in incoming) {
            println("Hey")
            frame.handleMessages()
        }

        // Handle session close
        if (currentConnection === currentSession.masterConnection) {
            currentSession.playerConnections.forEach { it.close() }
            println(shareSceneSessions.first() == currentSession)
            shareSceneSessions -= currentSession
        } else {
            currentSession.playerConnections -= currentConnection
        }
    }
}

private fun Frame.handleMessages() = when (this) {
    is Frame.Text -> println(readText())
    else -> Unit
}