package fr.olebo.domain.models.system

import androidx.compose.runtime.Immutable
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.util.Locale

@Serializable
@Immutable
enum class Language(@Serializable(with = LocaleSerializer::class) val locale: Locale) {
    English(Locale.ENGLISH), French(Locale.FRENCH);

    private companion object LocaleSerializer : KSerializer<Locale> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Locale", PrimitiveKind.STRING)

        override fun serialize(encoder: Encoder, value: Locale) {
            encoder.encodeString(value.toLanguageTag())
        }

        override fun deserialize(decoder: Decoder): Locale {
            return Locale.forLanguageTag(decoder.decodeString())
        }
    }
}