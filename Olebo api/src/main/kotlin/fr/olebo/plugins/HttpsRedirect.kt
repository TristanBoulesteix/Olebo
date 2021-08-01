package fr.olebo.plugins

import io.ktor.application.*
import io.ktor.features.*

fun Application.configureRedirect() {
    install(HttpsRedirect)
}