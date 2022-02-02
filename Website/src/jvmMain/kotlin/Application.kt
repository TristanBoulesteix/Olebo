package fr.olebo

import fr.olebo.plugins.configureFeatures
import fr.olebo.plugins.configureRouting
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import java.io.File
import java.security.KeyStore
import java.util.*

fun main() {
    val prop = Properties().apply {
        getStreamResource("config.properties").use(this::load)
    }

    val pwd: String by prop
    val keyStoreStream = getStreamResource("keystore.jks")
    val keyStoreFile = File("tmp_keystore.jks").apply {
        outputStream().use {
            keyStoreStream?.copyTo(it)
        }
    }

    val keyStore: KeyStore = KeyStore.getInstance(keyStoreFile, pwd.toCharArray())

    val environment = applicationEngineEnvironment {
        connector {
            port = 8080
        }
        sslConnector(
            keyStore = keyStore,
            keyAlias = "olebo",
            keyStorePassword = pwd::toCharArray,
            privateKeyPassword = pwd::toCharArray
        ) {
            port = 8443
            keyStorePath = keyStoreFile
        }
        module {
            configureFeatures()
            configureRouting()
        }
    }

    embeddedServer(Netty, environment).start(wait = true)
}

private fun getStreamResource(name: String) = ::main.javaClass.classLoader.getResourceAsStream(name)