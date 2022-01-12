package fr.olebo.sharescene

import kotlinx.serialization.Serializable

@Serializable
sealed class Message

@Serializable
class NewSessionCreated(@Serializable(with = IdSerializer::class) val id: Id, val code: String) : Message()

@Serializable
class NumberOfConnectedUser(val value: Int) : Message()

@Serializable
class BackgroundChanged(val image: Base64Image) : Message()

@Serializable
class NewMap(val backgroundImage: Base64Image, val tokens: List<Token>): Message()