package jdr.exia.view.utils

import jdr.exia.model.element.Element
import jdr.exia.model.element.Elements
import jdr.exia.model.utils.Point
import java.awt.*
import javax.swing.SwingUtilities
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@OptIn(ExperimentalContracts::class)
inline fun <T : Container> T.applyAndAddTo(
    parent: Container,
    constraints: Any? = null,
    block: T.() -> Unit
): T {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }

    this.apply(block)
    
    if (constraints != null)
        parent.add(this, constraints)
    else parent.add(this)

    return this
}

private val Dimension.area
    inline get() = width * height

/**
 * Compare area of dimensions
 */
operator fun Dimension.compareTo(dimension: Dimension) = this.area.compareTo(dimension.area)

/**
 * [GridBagConstraints] builder
 *
 * @return The [GridBagConstraints] built with the parameters
 */
fun gridBagConstraintsOf(
    gridx: Int? = null,
    gridy: Int? = null,
    gridHeight: Int? = null,
    gridWidth: Int? = null,
    weightx: Double? = null,
    weighty: Double? = null,
    fill: Int = GridBagConstraints.NONE,
    anchor: Int = GridBagConstraints.CENTER,
    insets: Insets? = null
) = GridBagConstraints().apply {
    gridx?.let { this.gridx = it }
    gridy?.let { this.gridy = it }
    gridHeight?.let { this.gridheight = it }
    gridWidth?.let { this.gridwidth = it }
    weightx?.let { this.weightx = it }
    weighty?.let { this.weighty = it }
    this.fill = fill
    this.anchor = anchor
    insets?.let { this.insets = it }
}

fun GridBagConstraints.copy(
    gridx: Int? = this.gridx,
    gridy: Int? = this.gridy,
    gridHeight: Int? = this.gridheight,
    gridWidth: Int? = this.gridwidth,
    weightx: Double? = this.weightx,
    weighty: Double? = this.weighty,
    fill: Int = this.fill,
    anchor: Int = this.anchor,
    insets: Insets? = this.insets
) = gridBagConstraintsOf(gridx, gridy, gridHeight, gridWidth, weightx, weighty, fill, anchor, insets)

val Component.windowAncestor: Window?
    inline get() = SwingUtilities.getWindowAncestor(this)

fun Graphics.fillCircleWithCenterCoordinates(x: Int, y: Int, radius: Int) =
    fillOval(x - radius, y - radius, radius * 2, radius * 2)

fun Graphics2D.drawCircleWithCenterCoordinates(x: Int, y: Int, radius: Int) {
    val previousStroke = this.stroke
    this.stroke = BasicStroke(3F)
    this.drawOval(x - radius, y - radius, radius * 2, radius * 2)
    this.stroke = previousStroke
}

/**
 * Return the first element to correspond to the given position or null
 */
fun Elements.getTokenFromPosition(point: Point) = point.let { (x, y) ->
    this.filter { it.hitBox.contains(x, y) }.maxByOrNull { it.priority }
}

/**
 * Get position of hitbox from coordinate clicked
 */
fun Element.positionOf(point: Point) =
    Point(point.x - hitBox.width / 2, point.y - hitBox.height / 2)

operator fun Dimension.component1() = this.width

operator fun Dimension.component2() = this.height

val screens: Array<GraphicsDevice>
    get() = GraphicsEnvironment.getLocalGraphicsEnvironment().screenDevices