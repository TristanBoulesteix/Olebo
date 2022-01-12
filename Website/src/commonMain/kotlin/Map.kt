package fr.olebo.sharescene

class Map(val backgroundImage: Base64Image) {
    val tokens = listOf<Token>()
}

data class Position(val x: Int, val y: Int)

data class Token(val image: Base64Image, val position: Position)

