package fr.olebo.sharescene

import io.ktor.http.cio.websocket.*
import java.util.concurrent.atomic.AtomicInteger

data class ShareSceneSession(
    val sessionId: String,
    val masterConnection: Connection,
    val playerConnections: MutableSet<Connection>
)

class Connection(session: DefaultWebSocketSession) : DefaultWebSocketSession by session {
    companion object {
        private var lastId = AtomicInteger(0)
    }

    val id = lastId.incrementAndGet()
}