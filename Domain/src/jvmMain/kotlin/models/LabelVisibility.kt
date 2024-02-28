package fr.olebo.domain.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
enum class LabelVisibility {
    OnlyForMaster, ForBoth, Hidden;

    val isVisible
        get() = this in setOf(OnlyForMaster, ForBoth)

    internal fun encode() = Json.encodeToString(this)

    internal companion object {
        operator fun get(json: String) = try {
            Json.decodeFromString(json)
        } catch (e: Exception) {
            OnlyForMaster
        }
    }
}