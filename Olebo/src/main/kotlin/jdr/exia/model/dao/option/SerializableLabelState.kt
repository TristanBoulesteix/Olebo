package jdr.exia.model.dao.option

import jdr.exia.localization.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
enum class SerializableLabelState(val text: String) {
    DISABLED(Strings[STR_LABEL_DISABLED]),
    ONLY_FOR_MASTER(Strings[STR_LABEL_VISIBLE_FOR_MASTER]),
    FOR_BOTH(Strings[STR_LABEL_VISIBLE]),
    HIDDEN(Strings[STR_LABEL_HIDDEN]);

    companion object {
        operator fun get(json: String) = try {
            Json.decodeFromString(json)
        } catch (e: Exception) {
            HIDDEN
        }
    }

    val isVisible
        get() = this in arrayOf(ONLY_FOR_MASTER, FOR_BOTH)

    val isEnabled
        get() = this != DISABLED

    /**
     * Encode a [SerializableLabelState] to json [String] to be uploaded to the database
     */
    fun encode() = Json.encodeToString(this)
}