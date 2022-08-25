@file:Suppress("HttpUrlsUsage")

package fr.olebo.sharescene

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

enum class UrlProtocol(val value: String, val port: Int) {
    HTTP("http", 8080), HTTPS("https", 443)
}

@JvmInline
@Serializable
value class URL(val value: String) {
    internal val urlParts
        get() = value.split("://")
}

val URL.security
    get() = when (urlParts[0]) {
        "https" -> UrlProtocol.HTTPS
        else -> UrlProtocol.HTTP
    }

val URL.domain
    get() = urlParts[1]