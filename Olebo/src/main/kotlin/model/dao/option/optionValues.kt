package model.dao.option

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import model.dao.internationalisation.*
import model.utils.toColor
import java.awt.Color as JColor

/**
 * Used to serialize [java.awt.Color] and put them into the database.
 *
 * This class is meant to be used as data of [CursorColor].
 */
@Serializable
data class Color(val r: Int, val g: Int, val b: Int) {
    override fun toString() = "(r = $r, g = $g, b = $b)"
}

@Suppress("ClassName")
@Serializable
sealed class CursorColor(
    val name: String,
    val contentCursorColor: Color,
    val borderCursorColor: Color
) {
    companion object {
        operator fun get(key: String) = try {
            Json.decodeFromString<CursorColor>(key)
        } catch (e: Exception) {
            BLACK_WHITE
        }
    }

    @Serializable
    object BLACK_WHITE : CursorColor(Strings[STR_BLACK_WITH_WHITE_BORDER], JColor.BLACK.toColor(), JColor.WHITE.toColor())

    @Serializable
    object WHITE_BLACK : CursorColor(Strings[STR_WHITE_WITH_BLACK_BORDER], JColor.WHITE.toColor(), JColor.BLACK.toColor())

    @Serializable
    object PURPLE : CursorColor(Strings[STR_PURPLE], JColor(168, 50, 143).toColor(), JColor(168, 50, 143).toColor())

    @Suppress("CanBeParameter")
    @Serializable
    class Custom(private val customColor: Color) : CursorColor(Strings[STR_CUSTOM_COLOR], customColor, customColor) {
        constructor(color: JColor) : this(color.toColor())
    }

    /**
     * Encode a [CursorColor] to json [String] to be uploaded to the database
     */
    fun encode() = Json.encodeToString(this)
}