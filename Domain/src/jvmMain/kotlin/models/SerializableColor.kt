package fr.olebo.domain.models

import androidx.compose.ui.graphics.Color
import fr.olebo.domain.serialization.ColorSerializer
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
@Polymorphic
sealed class SerializableColor(
    @Serializable(with = ColorSerializer::class) val contentColor: Color,
    @Serializable(with = ColorSerializer::class) val borderColor: Color
) {
    constructor(color: Color) : this(color, color)

    operator fun component1() = contentColor

    operator fun component2() = borderColor

    /**
     * Encode a [SerializableColor] to json [String] to be uploaded to the database
     */
    internal fun encode() = Json.encodeToString(this)

    @Serializable
    data object BlackWhite : SerializableColor(Color.Black, Color.White)

    @Serializable
    data object WhiteBlack : SerializableColor(Color.White, Color.Black)

    @Serializable
    data object BLACK : SerializableColor(Color.Black)

    @Serializable
    data object PURPLE : SerializableColor(Color(168, 50, 143))

    @Serializable
    data object YELLOW : SerializableColor(Color.Yellow)

    @Serializable
    data object RED : SerializableColor(Color.Red)

    @Serializable
    data object Unspecified : SerializableColor(Color.Unspecified)

    @Serializable
    class Custom(
        @Serializable(with = ColorSerializer::class) val color: Color,
    ) : SerializableColor(color)

    internal companion object {
        operator fun get(key: String) = try {
            Json.decodeFromString<SerializableColor>(key)
        } catch (e: Exception) {
            YELLOW
        }
    }
}
