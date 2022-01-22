package fr.olebo.sharescene

import fr.olebo.synchronizedConnectionsSet
import fr.olebo.synchronizedSet
import io.ktor.http.cio.websocket.*
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

data class ShareSceneSession(val masterConnection: Connection) {
    val sessionId: UUID = UUID.randomUUID()

    val playerConnections = synchronizedConnectionsSet()

    val urlCode: String

    lateinit var map: Map

    var cursor: CursorMoved.Cursor? = null

    init {
        val alphabet = 'a'..'z'
        val numbers = '1'..'9'

        val list = alphabet + numbers

        urlCode = buildString {
            do {
                clear()

                repeat(6) {
                    append(list.random())
                }
            } while (toString() in ids)
        }.also { ids += it }
    }

    suspend fun sendToMaster(message: Message) = masterConnection.send(message)

    suspend fun sendToPlayers(message: Message) = playerConnections.forEach { it.send(message) }

    fun getPlayers() = playerConnections.map { Player(it.name) }

    companion object {
        private val ids = synchronizedSet<String>()
    }
}

sealed interface Connection : DefaultWebSocketSession {
    val id: Int

    val name: String
}

private class ConnectionImpl(session: DefaultWebSocketSession, override val name: String = "") : Connection,
    DefaultWebSocketSession by session {
    companion object {
        private val lastId = AtomicInteger(0)
    }

    override val id = lastId.incrementAndGet()
}

fun DefaultWebSocketSession.Connection(): Connection = ConnectionImpl(this)

fun DefaultWebSocketSession.Connection(name: String): Connection = ConnectionImpl(this, name)

suspend infix fun MutableSet<ShareSceneSession>.destroy(session: ShareSceneSession) {
    session.playerConnections.onEach { it.close() }.clear()
    remove(session)
}