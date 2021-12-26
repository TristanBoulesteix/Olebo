package fr.olebo.sharescene

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

internal actual object IdSerializer : KSerializer<Id> {
    override fun deserialize(decoder: Decoder): Id {
        TODO("Not yet implemented")
    }

    override val descriptor: SerialDescriptor
        get() = TODO("Not yet implemented")

    override fun serialize(encoder: Encoder, value: Id) {
        TODO("Not yet implemented")
    }
}