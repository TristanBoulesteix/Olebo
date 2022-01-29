package fr.olebo.sharescene

import kotlinx.serialization.Serializable

internal typealias Color = Triple<Int, Int, Int>

@Serializable
sealed class Message

@Serializable
data class NewSessionCreated(
    @Serializable(with = IdSerializer::class) val id: Id,
    val code: String,
    val minimalOleboVersion: Int
) : Message()

@Serializable
class PlayerAddedOrRemoved(val users: List<Player>) : Message() {
    override fun toString() = toStringWithClassName("(${users.size} player(s))")
}

@Serializable
class NewMap(val backgroundImage: Base64Image, val tokens: List<Token>) : Message() {
    override fun toString() = toStringWithClassName("with ${tokens.size} token(s)")
}

@Serializable
class TokenStateChanged(val tokens: List<Token>) : Message() {
    override fun toString() = toStringWithClassName("currently (${tokens.size} token(s)")
}

@Serializable
object ConnectionRefused : Message() {
    override fun toString() = toStringWithClassName()
}

@Serializable
data class CursorMoved(val cursor: Cursor) : Message() {
    @Serializable
    data class Cursor(val position: Position, val color: Color, val borderColor: Color)

    constructor(position: Position, color: Color, borderColor: Color) : this(Cursor(position, color, borderColor))
}

@Serializable
object CursorHidden : Message() {
    override fun toString() = toStringWithClassName()
}

private fun Message.toStringWithClassName(appended: String? = null) = buildString {
    append(this@toStringWithClassName::class.simpleName)

    if (appended != null) {
        append(' ')
        append(appended)
    }
}