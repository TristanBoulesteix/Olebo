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
    Json.decodeFromString<Message>(readText())
} catch (t: Throwable) {
    t.printStackTrace()
    null
}

suspend fun initWebsocket(
    client: HttpClient,
    path: String,
    socketBlock: suspend DefaultClientWebSocketSession.(manager: ShareSceneManager, setSessionCode: (String) -> Unit) -> Unit,
    onFailure: (manager: ShareSceneManager) -> Unit
) {
    ShareSceneManager(client, path, socketBlock, onFailure).use {
        it.initWebsocket()
    }
}