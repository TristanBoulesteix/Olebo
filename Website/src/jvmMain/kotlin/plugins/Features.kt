package fr.olebo.plugins

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.httpsredirect.*
import io.ktor.server.websocket.*

fun Application.configureFeatures(productionMode: Boolean = true) {
    if (productionMode)
        install(HttpsRedirect)
    install(ContentNegotiation) {
        json()
    }
    install(WebSockets)
    install(CallLogging)
}