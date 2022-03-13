package fr.olebo

import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main(vararg args: String) {
    val environment = if (args.isNotEmpty() && args.first() == "dev") devEnvironment else productionEnvironment

    embeddedServer(Netty, environment).start(wait = true)
}