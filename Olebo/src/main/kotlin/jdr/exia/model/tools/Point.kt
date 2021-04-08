package jdr.exia.model.tools

import java.awt.Rectangle
import java.awt.Point as JPoint

/**
 * Coordinates of an element
 */
data class Point(val x: Int, val y: Int) {
    constructor(point: JPoint) : this(point.x, point.y)

    fun toJPoint() = JPoint(x, y)

    operator fun contains(rectangle: Rectangle) = rectangle.contains(x, y)

    operator fun minus(point: Point) = Point(x - point.x, y - point.y)

    operator fun plus(point: Point) = Point(x + point.x, y + point.y)
}