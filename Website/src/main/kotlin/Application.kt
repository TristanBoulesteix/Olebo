package fr.olebo

import fr.olebo.plugins.configureRouting
import fr.olebo.plugins.configureSerialization
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import java.io.File
import java.security.KeyStore

fun main() {
    val keyStoreFile = File("")
    val keyStore: KeyStore = KeyStore.getInstance(keyStoreFile, "".toCharArray())

    val environment = applicationEngineEnvironment {
        connector {
            port = 8080
        }
        sslConnector(
            keyStore = keyStore,
            keyAlias = "alias",
            keyStorePassword = { "".toCharArray() },
            privateKeyPassword = { "".toCharArray() }
        ) {
            port = 0
            keyStorePath = keyStoreFile
        }
        module {
            configureRouting()
            configureSerialization()
        }
    }

    embeddedServer(Netty, environment).start(wait = true)
}