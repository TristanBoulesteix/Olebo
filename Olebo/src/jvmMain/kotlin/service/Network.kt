package jdr.exia.service

import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.websocket.*
import io.ktor.serialization.kotlinx.json.*

val client
    get() = HttpClient {
        install(ContentNegotiation) {
            json()
        }
    }

val socketClient
    get() = client.config {
        install(WebSockets)
    }