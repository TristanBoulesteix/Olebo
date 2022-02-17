package fr.olebo.sharescene.websocket

import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.websocket.*
import io.ktor.serialization.kotlinx.json.*

val client
    get() = HttpClient {
        install(ContentNegotiation) {
            json()
        }
        install(WebSockets)
    }