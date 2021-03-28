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
sealed class SerializableColor(
    val name: String,
    @Serializable(with = ColorAsStringSerializer::class) val contentColor: Color,
    @Serializable(with = ColorAsStringSerializer::class) val borderColor: Color
) {
    companion object {
        operator fun get(key: String) = try {
            Json.decodeFromString<SerializableColor>(key)
        } catch (e: Exception) {
            YELLOW
        }
    }

    constructor(name: String, color: Color) : this(name, color, color)

    @Serializable
    object BLACK_WHITE :
        SerializableColor(StringLocale[STR_BLACK_WITH_WHITE_BORDER], Color.BLACK, Color.WHITE)

    @Serializable
    object WHITE_BLACK :
        SerializableColor(StringLocale[STR_WHITE_WITH_BLACK_BORDER], Color.WHITE, Color.BLACK)

    @Serializable
    object BLACK :
        SerializableColor(StringLocale[STR_BLACK], Color.BLACK)

    @Serializable
    object PURPLE : SerializableColor(StringLocale[STR_PURPLE], Color(168, 50, 143))

    @Serializable
    object YELLOW : SerializableColor(StringLocale[STR_YELLOW], Color.YELLOW)

    @Serializable
    object RED : SerializableColor(StringLocale[STR_RED], Color.RED)

    @Suppress("CanBeParameter")
    @Serializable
    class Custom(@Serializable(with = ColorAsStringSerializer::class) private val customColor: Color) :
        SerializableColor(StringLocale[STR_CUSTOM_COLOR], customColor)

    /**
     * Encode a [SerializableColor] to json [String] to be uploaded to the database
     */
    fun encode() = Json.encodeToString(this)
}

private object ColorAsStringSerializer : KSerializer<Color> {
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