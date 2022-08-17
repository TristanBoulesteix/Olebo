@file:Suppress("HttpUrlsUsage")

package fr.olebo.sharescene

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

enum class UrlProtocol(val value: String) {
    HTTP("http"), HTTPS("https")
}

@JvmInline
@Serializable
value class URL(private val url: String) {
    internal val urlParts
        get() = url.split("://")
}

val URL.security
    get() = when (urlParts[0]) {
        "https" -> UrlProtocol.HTTPS
        else -> UrlProtocol.HTTP
    }

val URL.domain
    get() = urlParts[1]