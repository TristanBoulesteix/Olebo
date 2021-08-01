package fr.olebo

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import fr.olebo.plugins.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        // configureRedirect()
        configureRouting()
        configureSerialization()
    }.start(wait = true)
}
