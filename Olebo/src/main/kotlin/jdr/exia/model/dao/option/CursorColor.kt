package jdr.exia.model.dao.option

import jdr.exia.localization.*
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import java.awt.Color

fun Color.toFormatedString() = "(r = $red, g = $green, b = $blue)"

@Suppress("ClassName")
@Polymorphic
@Serializable
sealed class CursorColor(
    val name: String,
    @Serializable(with = ColorAsStringSerializer::class) val contentCursorColor: Color,
    @Serializable(with = ColorAsStringSerializer::class) val borderCursorColor: Color
) {
    companion object {
        operator fun get(key: String) = try {
            Json.decodeFromString<CursorColor>(key)
        } catch (e: Exception) {
            RED
        }
    }

    object ColorAsStringSerializer : KSerializer<Color> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Color", PrimitiveKind.STRING)

        override fun serialize(encoder: Encoder, value: Color) {
            val string = value.rgb.toString(16).padStart(6, '0')
            encoder.encodeString(string)
        }

        override fun deserialize(decoder: Decoder): Color {
            val string = decoder.decodeString()
            return Color(string.toInt(16))
        }
    }

    constructor(name: String, color: Color) : this(name, color, color)

    @Serializable
    object BLACK_WHITE :
        CursorColor(Strings[STR_BLACK_WITH_WHITE_BORDER], Color.BLACK, Color.WHITE)

    @Serializable
    object WHITE_BLACK :
        CursorColor(Strings[STR_WHITE_WITH_BLACK_BORDER], Color.WHITE, Color.BLACK)

    @Serializable
    object PURPLE : CursorColor(Strings[STR_PURPLE], Color(168, 50, 143))

    @Serializable
    object YELLOW : CursorColor(Strings[STR_YELLOW], Color.YELLOW)

    @Serializable
    object RED : CursorColor(Strings[STR_RED], Color.RED)

    @Suppress("CanBeParameter")
    @Serializable
    class Custom(@Serializable(with = CursorColor.ColorAsStringSerializer::class) private val customColor: Color) :
        CursorColor(Strings[STR_CUSTOM_COLOR], customColor)

    /**
     * Encode a [CursorColor] to json [String] to be uploaded to the database
     */
    fun encode() = Json.encodeToString(this)
}