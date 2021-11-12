package jdr.exia.model.dao.option

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import jdr.exia.localization.*
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json

@Suppress("ClassName")
@Polymorphic
@Serializable
sealed class SerializableColor(
    private val nameKey: String,
    @Serializable(with = ColorAsStringSerializer::class) val contentColor: Color,
    @Serializable(with = ColorAsStringSerializer::class) val borderColor: Color
) {
    companion object {
        @OptIn(ExperimentalSerializationApi::class)
        operator fun get(key: String) = try {
            Json.decodeFromString<SerializableColor>(key)
        } catch (e: Exception) {
            YELLOW
        }
    }

    val name
        get() = StringLocale[nameKey]

    constructor(nameKey: String, color: Color) : this(nameKey, color, color)

    operator fun component1() = contentColor

    operator fun component2() = borderColor

    @Serializable
    object BLACK_WHITE :
        SerializableColor(STR_BLACK_WITH_WHITE_BORDER, Color.Black, Color.White)

    @Serializable
    object WHITE_BLACK :
        SerializableColor(STR_WHITE_WITH_BLACK_BORDER, Color.White, Color.Black)

    @Serializable
    object BLACK :
        SerializableColor(STR_BLACK, Color.Black)

    @Serializable
    object PURPLE : SerializableColor(STR_PURPLE, Color(168, 50, 143))

    @Serializable
    object YELLOW : SerializableColor(STR_YELLOW, Color.Yellow)

    @Serializable
    object RED : SerializableColor(STR_RED, Color.Red)

    @Serializable
    class Custom(@Serializable(with = ColorAsStringSerializer::class) private val customColor: Color) :
        SerializableColor(STR_CUSTOM_COLOR, customColor) {
        companion object {
            val default
                get() = Custom(Color.Unspecified)
        }

        val color
            get() = customColor
    }

    /**
     * Encode a [SerializableColor] to json [String] to be uploaded to the database
     */
    @OptIn(ExperimentalSerializationApi::class)
    fun encode() = Json.encodeToString(this)

    override fun toString() = name

    private object ColorAsStringSerializer : KSerializer<Color> {
        override val descriptor: SerialDescriptor
            get() = PrimitiveSerialDescriptor("Color", PrimitiveKind.STRING)

        override fun serialize(encoder: Encoder, value: Color) = encoder.encodeString(value.toArgb().toString())

        override fun deserialize(decoder: Decoder): Color = Color(decoder.decodeString().toInt())
    }
}