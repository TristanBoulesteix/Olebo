package fr.olebo.sharescene

import io.ktor.http.cio.websocket.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

suspend fun WebSocketSession.send(message: Message) = send(Json.encodeToString(message))

fun Frame.Text.getMessageOrNull() = try {
    Json.decodeFromString<Message>(this.readText())
} catch (t: Throwable) {
    null
}