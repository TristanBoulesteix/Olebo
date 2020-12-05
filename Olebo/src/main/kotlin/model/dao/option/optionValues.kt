package model.dao.option

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import model.utils.toColor
import java.awt.Color as JColor

@Serializable
data class Color(val r: Int, val g: Int, val b: Int)

@Suppress("ClassName")
@Serializable
sealed class CursorColor(
    val name: String,
    val contentCursorColor: Color,
    val borderCursorColor: Color
) {
    companion object {
        operator fun get(key: String) = when (key) {
            "BLACK_WHITE" -> BLACK_WHITE
            "WHITE_BLACK" -> WHITE_BLACK
            "PURPLE" -> PURPLE
            else -> try {
                Json.decodeFromString(key)
            } catch (e: Exception) {
                BLACK_WHITE
            }
        }
    }

    @Serializable
    object BLACK_WHITE : CursorColor("Black with white borders", JColor.BLACK.toColor(), JColor.WHITE.toColor())

    @Serializable
    object WHITE_BLACK : CursorColor("White with black borders", JColor.WHITE.toColor(), JColor.BLACK.toColor())

    @Serializable
    object PURPLE : CursorColor("Purple", JColor(168, 50, 143).toColor(), JColor(168, 50, 143).toColor())

    @Suppress("CanBeParameter")
    @Serializable
    class CUSTOM(private val customColor: Color) : CursorColor("Custom", customColor, customColor) {
        constructor(color: JColor) : this(color.toColor())
    }

    fun encode() = Json.encodeToString(this)
}