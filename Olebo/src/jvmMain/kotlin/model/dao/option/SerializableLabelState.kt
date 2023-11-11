package jdr.exia.model.dao.option

import jdr.exia.localization.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
enum class SerializableLabelState(private val stringKey: String) {
    ONLY_FOR_MASTER(STR_LABEL_VISIBLE_FOR_MASTER),
    FOR_BOTH(STR_LABEL_VISIBLE),
    HIDDEN(STR_LABEL_HIDDEN);

    companion object {
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
    fun encode() = Json.encodeToString(this)

    override fun toString() = StringLocale[stringKey]
}