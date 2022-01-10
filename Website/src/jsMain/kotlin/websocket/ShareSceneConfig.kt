package fr.olebo.sharescene.websocket

import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.websocket.*

val client
    get() = HttpClient {
        install(JsonFeature) {
            serializer = KotlinxSerializer()
        }
        install(WebSockets)
    }