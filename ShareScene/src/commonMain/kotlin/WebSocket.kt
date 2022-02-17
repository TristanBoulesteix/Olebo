package fr.olebo.sharescene

import fr.olebo.sharescene.connection.ConnectionError
import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.utils.io.core.*
import io.ktor.websocket.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

suspend fun WebSocketSession.send(message: Message) = send(Json.encodeToString(message))

fun Frame.Text.getMessageOrNull() = try {
    Json.decodeFromString<Message>(readText())
} catch (t: Throwable) {
    t.printStackTrace()
    null
}

suspend fun initWebsocket(
    client: HttpClient,
    path: String,
    socketBlock: suspend DefaultClientWebSocketSession.(manager: ShareSceneManager, setSessionCode: (String) -> Unit) -> Unit,
    onFailure: (cause: ConnectionError) -> Unit
) {
    ShareSceneManager(client, path, socketBlock, onFailure).use {
        it.initWebsocket()
    }
}