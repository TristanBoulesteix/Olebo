package fr.olebo

import fr.olebo.plugins.configureRouting
import fr.olebo.plugins.configureSerialization
import fr.olebo.plugins.httpsRedirect
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import java.io.File
import java.security.KeyStore
import java.util.*

fun main() {
    val prop = Properties().apply {
        File("/var/opt/olebo/config.properties").inputStream().use(this::load)
    }

    val pwd: String by prop
    val keyStoreFile = File("/etc/letsencrypt/live/olebo.fr/keystore.jks")
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
            httpsRedirect()
            configureRouting()
            configureSerialization()
        }
    }

    embeddedServer(Netty, environment).start(wait = true)
}