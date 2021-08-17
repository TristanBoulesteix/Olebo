package fr.olebo

import fr.olebo.plugins.configureRouting
import fr.olebo.plugins.configureSerialization
import fr.olebo.update.OLEBO_RELEASES_DIRECTORY
import fr.olebo.update.ReleaseDirectory
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import java.io.File

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", watchPaths = listOf("Website", "Update")) {
        println(ReleaseDirectory.getFromParent(File(OLEBO_RELEASES_DIRECTORY)))
        configureRouting()
        configureSerialization()
    }.start(wait = true)
}