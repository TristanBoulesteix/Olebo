package fr.olebo.sharescene

import fr.olebo.synchronizedConnectionsSet
import io.ktor.http.cio.websocket.*
import java.util.concurrent.atomic.AtomicInteger

data class ShareSceneSession(
    val sessionId: String,
    val masterConnection: Connection,
    val playerConnections: MutableSet<Connection> = synchronizedConnectionsSet()
)

interface Connection : DefaultWebSocketSession {
    val id: Int
}

private class ConnectionImpl(session: DefaultWebSocketSession) : Connection, DefaultWebSocketSession by session {
    companion object {
        private val lastId = AtomicInteger(0)
    }

    override val id = lastId.incrementAndGet()
}

fun DefaultWebSocketSession.Connection(): Connection = ConnectionImpl(this)

suspend infix fun MutableSet<ShareSceneSession>.destroy(session: ShareSceneSession) {
    session.playerConnections.onEach { it.close() }.clear()
    remove(session)
}