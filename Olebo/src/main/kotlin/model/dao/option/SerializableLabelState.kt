package jdr.exia.model.dao.option

import jdr.exia.localization.STR_LABEL_HIDDEN
import jdr.exia.localization.STR_LABEL_VISIBLE
import jdr.exia.localization.STR_LABEL_VISIBLE_FOR_MASTER
import jdr.exia.localization.StringLocale
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
enum class SerializableLabelState(val text: String) {
    ONLY_FOR_MASTER(StringLocale[STR_LABEL_VISIBLE_FOR_MASTER]),
    FOR_BOTH(StringLocale[STR_LABEL_VISIBLE]),
    HIDDEN(StringLocale[STR_LABEL_HIDDEN]);

    companion object {
        @OptIn(ExperimentalSerializationApi::class)
        operator fun get(json: String) = try {
            Json.decodeFromString(json)
        } catch (e: Exception) {
            ONLY_FOR_MASTER
        }
    }

    val isVisible
        get() = this in arrayOf(ONLY_FOR_MASTER, FOR_BOTH)

    /**
     * Encode a [SerializableLabelState] to json [String] to be uploaded to the database
     */
    @OptIn(ExperimentalSerializationApi::class)
    fun encode() = Json.encodeToString(this)
}