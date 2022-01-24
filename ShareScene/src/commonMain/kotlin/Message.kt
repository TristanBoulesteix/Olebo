package fr.olebo.sharescene

import kotlinx.serialization.Serializable

internal typealias Color = Triple<Int, Int, Int>

@Serializable
sealed class Message

@Serializable
class NewSessionCreated(
    @Serializable(with = IdSerializer::class) val id: Id,
    val code: String,
    val minimalOleboVersion: Int
) : Message()

@Serializable
class PlayerAddedOrRemoved(val users: List<Player>) : Message()

@Serializable
class NewMap(val backgroundImage: Base64Image, val tokens: List<Token>) : Message()

@Serializable
class TokenStateChanged(val tokens: List<Token>) : Message()

@Serializable
object ConnectionRefused : Message()

@Serializable
class CursorMoved(val cursor: Cursor) : Message() {
    @Serializable
    data class Cursor(val position: Position, val color: Color, val borderColor: Color)

    constructor(position: Position, color: Color, borderColor: Color) : this(Cursor(position, color, borderColor))
}

@Serializable
object CursorHidden : Message()