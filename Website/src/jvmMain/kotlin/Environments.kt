package fr.olebo

import fr.olebo.plugins.configureFeatures
import fr.olebo.plugins.configureRouting
import io.ktor.server.engine.*
import java.io.File
import java.security.KeyStore
import java.util.*

val productionEnvironment: ApplicationEngineEnvironment
    get() {
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

        return applicationEngineEnvironment {
            commonEnvironment(productionMode = true)
            sslConnector(
                keyStore = keyStore,
                keyAlias = "olebo",
                keyStorePassword = pwd::toCharArray,
                privateKeyPassword = pwd::toCharArray
            ) {
                port = 8443
                keyStorePath = keyStoreFile
            }
        }
    }

val devEnvironment: ApplicationEngineEnvironment
    get() = applicationEngineEnvironment {
        commonEnvironment(productionMode = false)
    }

private fun ApplicationEngineEnvironmentBuilder.commonEnvironment(productionMode: Boolean) {
    connector {
        port = 8080
    }
    module {
        configureFeatures(productionMode)
        configureRouting()
    }
}

private fun getStreamResource(name: String) = ::main.javaClass.classLoader.getResourceAsStream(name)