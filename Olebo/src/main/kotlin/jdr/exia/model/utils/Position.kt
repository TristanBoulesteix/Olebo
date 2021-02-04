package jdr.exia.model.utils

import java.awt.Point
import java.awt.Rectangle

/**
 * Coordinates of an element
 */
data class Position(val x: Int, val y: Int) {
    constructor(point: Point) : this(point.x, point.y)

    fun toPoint() = Point(x, y)

    operator fun contains(rectangle: Rectangle) = rectangle.contains(x, y)

    operator fun minus(position: Position) = Position(x - position.x, y - position.y)

    operator fun plus(position: Position) = Position(x + position.x, y + position.y)
}