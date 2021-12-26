package fr.olebo.sharescene

import io.ktor.client.*
import io.ktor.client.features.websocket.*
import io.ktor.http.cio.websocket.*
import io.ktor.utils.io.core.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

suspend fun WebSocketSession.send(message: Message) = send(Json.encodeToString(message))

fun Frame.Text.getMessageOrNull() = try {
    Json.decodeFromString<Message>(this.readText())
} catch (t: Throwable) {
    null
}

suspend fun initWebsocket(
    client: HttpClient,
    socketBlock: suspend DefaultClientWebSocketSession.(manager: ShareSceneManager, setSessionCode: (String) -> Unit) -> Unit,
    onFailure: (manager: ShareSceneManager) -> Unit
) {
    ShareSceneManager(client, socketBlock, onFailure).use {
        it.initWebsocket()
    }
}