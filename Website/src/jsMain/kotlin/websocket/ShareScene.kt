package fr.olebo.sharescene.websocket

import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.websocket.*
import io.ktor.utils.io.core.*

private val client
    get() = HttpClient {
        install(JsonFeature) {
            serializer = KotlinxSerializer()
        }
        install(WebSockets)
    }

suspend fun start(sessionCode: String) = client.use {
    it.webSocket(host = "localhost", port = 8080, path = "share-scene/$sessionCode") {
        for (frame in incoming) {
            println(frame)
        }
    }
}