package fr.olebo

import fr.olebo.plugins.configureRouting
import fr.olebo.plugins.configureSerialization
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    val environment = applicationEngineEnvironment {
        connector {
            port = 8080
        }
        module {
            configureRouting()
            configureSerialization()
        }
    }

    embeddedServer(Netty, environment).start(wait = true)
}