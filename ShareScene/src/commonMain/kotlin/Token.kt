package fr.olebo.sharescene

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline
import kotlin.math.PI

@Serializable
data class Position(val x: Int, val y: Int)

@Serializable
data class Label(val text: String, val color: Color)

@Serializable
@JvmInline
value class Angle(private val degrees: Float) {
    val radians
        get() = degrees * (PI / 180)
}

@Serializable
data class Token(val image: Base64Image, val position: Position, val size: Int, val rotation: Angle, val label: Label? = null)