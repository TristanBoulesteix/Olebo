package fr.olebo.sharescene.connection

import io.ktor.utils.io.*
import io.ktor.websocket.*

internal class ConnectionException(val error: ConnectionError) : Throwable()

internal fun Throwable.getConnectionError() = when (this) {
    is CancellationException -> ConnectionError.Canceled
    is ConnectionException -> error
    else -> ConnectionError.ConnectionFailed
}

@Suppress("UnusedReceiverParameter")
fun WebSocketSession.triggerError(error: ConnectionError): Nothing = throw ConnectionException(error)