package fr.olebo.sharescene

import fr.olebo.log.log
import io.ktor.http.cio.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.yield
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

class ShareSceneSession(private val masterConnection: Connection, val urlCode: String) {
    val sessionId: UUID = UUID.randomUUID()

    val playerConnections = synchronizedConnectionsSet()

    val mutex = Mutex()

    lateinit var map: Map

    var cursor: CursorMoved.Cursor? = null

    val log by masterConnection::log

    suspend fun sendToMaster(message: Message) = masterConnection.send(message)

    suspend fun sendToPlayers(message: Message) {
        mutex.withLock { playerConnections.forEach { it.send(message) } }
        log.info("Message sent to players of session $sessionId: $message.")
    }

    suspend fun getPlayers() = mutex.withLock { playerConnections.map { Player(it.name) } }
}

sealed interface Connection : DefaultWebSocketServerSession {
    val id: Int

    val name: String
}

private class ConnectionImpl(session: DefaultWebSocketServerSession, override val name: String) : Connection,
    DefaultWebSocketServerSession by session {
    companion object {
        private val lastId = AtomicInteger(0)
    }

    override val id = lastId.incrementAndGet()
}

fun DefaultWebSocketServerSession.Connection(name: String = ""): Connection = ConnectionImpl(this, name)

suspend infix fun MutableSet<ShareSceneSession>.destroy(session: ShareSceneSession) {
    session.mutex.withLock {
        session.playerConnections.onEach { it.close() }.clear()
    }

    remove(session)
    SessionIdManager.removeId(session.urlCode)

    session.log.info("Session ${session.sessionId} destroyed.")
    session.log.debug("$size sessions are currently running.")
}

private object SessionIdManager {
    private val mutex = Mutex()

    private val ids = synchronizedSet<String>()

    suspend fun generateUniqueId() = mutex.withLock {
        val alphabet = 'a'..'z'
        val numbers = '1'..'9'

        val list = alphabet + numbers

        buildString {
            do {
                clear()

                repeat(6) {
                    yield()
                    append(list.random())
                }

                yield()
            } while (toString() in ids)
        }.also { ids += it }
    }

    suspend fun removeId(id: String) = mutex.withLock { ids -= id }
}

suspend fun Connection.createSession() = ShareSceneSession(this, SessionIdManager.generateUniqueId())