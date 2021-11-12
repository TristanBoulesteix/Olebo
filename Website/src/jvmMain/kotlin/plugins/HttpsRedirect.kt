package fr.olebo.plugins

import io.ktor.application.*
import io.ktor.features.*

fun Application.httpsRedirect() = install(HttpsRedirect)