package fr.olebo.sharescene

import fr.olebo.synchronizedConnectionsSet
import io.ktor.http.cio.websocket.*
import java.util.concurrent.atomic.AtomicInteger

data class ShareSceneSession(
    val sessionId: String,
    val masterConnection: Connection,
    val playerConnections: MutableSet<Connection> = synchronizedConnectionsSet()
)

class Connection(session: DefaultWebSocketSession) : DefaultWebSocketSession by session {
    companion object {
        private var lastId = AtomicInteger(0)
    }

    val id = lastId.incrementAndGet()
}

suspend infix fun MutableSet<ShareSceneSession>.destroy(session: ShareSceneSession) {
    session.playerConnections.onEach { it.close() }.clear()
    remove(session)
}