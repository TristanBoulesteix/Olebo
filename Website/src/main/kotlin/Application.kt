package fr.olebo

import fr.olebo.plugins.configureRouting
import fr.olebo.plugins.configureSerialization
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", watchPaths = listOf("Website", "Update")) {
        configureRouting()
        configureSerialization()
    }.start(wait = true)
}