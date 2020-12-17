package jdr.exia.updater

import jdr.exia.defaultLocale
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class UpdateOptions(val restart: Boolean = false, val localeCode: String = defaultLocale.language) {
    fun toQuotedString() = Json.encodeToString(this).replace("\"", "\\\"")
}