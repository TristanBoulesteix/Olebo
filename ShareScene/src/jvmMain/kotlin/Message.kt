package fr.olebo.sharescene

import kotlinx.serialization.Serializable
import java.util.*

@Serializable
sealed class Message

@Serializable
class NewSessionCreated(@Serializable(with = UUIDSerializer::class) val id: UUID) : Message()
