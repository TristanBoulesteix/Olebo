package jdr.exia.updater

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class UpdateOptions(val restart: Boolean = false, val localeCode: String) {
    override fun toString() = Json.encodeToString(this)
}