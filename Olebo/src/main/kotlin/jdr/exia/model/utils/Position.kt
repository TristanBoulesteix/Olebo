package jdr.exia.model.utils

import java.awt.Point

/**
 * Coordinates of an element
 */
data class Position(val x: Int, val y: Int) {
    constructor(point: Point) : this(point.x, point.y)

    fun toPoint() = Point(x, y)
}