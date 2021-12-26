package fr.olebo.sharescene

import kotlinx.serialization.Serializable

@Serializable
sealed class Message

@Serializable
class NewSessionCreated(@Serializable(with = IdSerializer::class) val id: Id, val code: String) : Message()