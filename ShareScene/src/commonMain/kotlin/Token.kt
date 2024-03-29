package fr.olebo.sharescene

import kotlinx.serialization.Serializable

@Serializable
data class Position(val x: Int, val y: Int)

@Serializable
data class Label(val text: String, val color: Color)

@Serializable
data class Token(val image: Base64Image, val position: Position, val size: Int, val label: Label? = null)