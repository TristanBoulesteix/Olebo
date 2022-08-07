package jdr.exia.view.tools

import androidx.compose.ui.geometry.Offset
import jdr.exia.model.element.Element
import java.awt.*
import javax.swing.SwingUtilities

private val Dimension.area
    inline get() = width * height

/**
 * Compare area of dimensions
 */
operator fun Dimension.compareTo(dimension: Dimension) = this.area.compareTo(dimension.area)

val Component.windowAncestor: Window?
    inline get() = SwingUtilities.getWindowAncestor(this)

/**
 * Return the first element to correspond to the given position or null
 */
fun List<Element>.getTokenFromPosition(point: Offset) = point.let { (x, y) ->
    this.filter { it.hitBox.contains(x.toInt(), y.toInt()) }.maxByOrNull { it.priority }
}

/**
 * Get position of hitbox from coordinate clicked
 */
fun Element.positionOf(offset: Offset) =
    Offset(offset.x - hitBox.width / 2f, offset.y - hitBox.height / 2f)

operator fun Dimension.component1() = this.width

operator fun Dimension.component2() = this.height

val screens: Array<GraphicsDevice>
    get() = GraphicsEnvironment.getLocalGraphicsEnvironment().screenDevices