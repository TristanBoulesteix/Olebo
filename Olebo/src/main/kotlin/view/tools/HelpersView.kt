package jdr.exia.view.tools

import androidx.compose.ui.geometry.Offset
import jdr.exia.model.element.Element
import java.awt.*
import java.awt.image.BufferedImage
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

val Component.windowAncestor: Window?
    inline get() = SwingUtilities.getWindowAncestor(this)

fun Graphics.fillCircleWithCenterCoordinates(x: Float, y: Float, radius: Int) =
    fillOval((x - radius).toInt(), (y - radius).toInt(), radius * 2, radius * 2)

fun Graphics2D.drawCircleWithCenterCoordinates(x: Float, y: Float, radius: Int) {
    val previousStroke = this.stroke
    this.stroke = BasicStroke(3F)
    this.drawOval((x - radius).toInt(), (y - radius).toInt(), radius * 2, radius * 2)
    this.stroke = previousStroke
}

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

fun BufferedImage.rotateImage(rotationAngle: Float): BufferedImage {
    val theta = Math.PI * 2 / 360 * rotationAngle
    val width = width
    val height = height

    val dest: BufferedImage = if (rotationAngle == 90f || rotationAngle == 270f) {
        BufferedImage(height, width, type)
    } else {
        BufferedImage(width, height, type)
    }

    val graphics2D = dest.createGraphics()

    when (rotationAngle) {
        90f -> {
            graphics2D.translate((height - width) / 2, (height - width) / 2)
            graphics2D.rotate(theta, (height / 2).toDouble(), (width / 2).toDouble())
        }
        270f -> {
            graphics2D.translate((width - height) / 2, (width - height) / 2)
            graphics2D.rotate(theta, (height / 2).toDouble(), (width / 2).toDouble())
        }
        else -> {
            graphics2D.translate(0, 0)
            graphics2D.rotate(theta, (width / 2).toDouble(), (height / 2).toDouble())
        }
    }

    graphics2D.drawRenderedImage(this, null)

    return dest
}