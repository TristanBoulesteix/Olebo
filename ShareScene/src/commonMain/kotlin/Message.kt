package fr.olebo.sharescene

import fr.olebo.sharescene.map.Base64Image
import kotlinx.serialization.Serializable

@Serializable
sealed class Message

@Serializable
class NewSessionCreated(@Serializable(with = IdSerializer::class) val id: Id, val code: String) : Message()

@Serializable
class NumberOfConnectedUser(val value: Int) : Message()

@Serializable
class BackgroundChanged(val image: Base64Image) : Message() {
    val cssBase64ImageCode
        get() = "data:image/jpeg;base64,${image.value}"
}