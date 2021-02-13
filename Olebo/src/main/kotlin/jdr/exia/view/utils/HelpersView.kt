package jdr.exia.view.utils

import jdr.exia.localization.STR_CANCEL
import jdr.exia.localization.STR_CONFIRM
import jdr.exia.localization.STR_WARNING
import jdr.exia.localization.Strings
import jdr.exia.model.element.Element
import jdr.exia.model.utils.Elements
import jdr.exia.model.utils.Point
import java.awt.*
import java.awt.event.ItemEvent
import javax.swing.JButton
import javax.swing.JCheckBox
import javax.swing.JOptionPane
import javax.swing.SwingUtilities
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Show a popup with a message
 */
fun showPopup(message: String, parent: Component? = null, isError: Boolean = false) = JOptionPane.showMessageDialog(
    parent,
    message,
    Strings[STR_WARNING],
    if (!isError) JOptionPane.INFORMATION_MESSAGE else JOptionPane.ERROR_MESSAGE
)

inline fun showConfirmMessage(
    parent: Component? = null,
    message: Any,
    title: String,
    confirm: Boolean = false,
    crossinline okAction: () -> Unit
) {
    val ok = JButton(Strings[STR_CONFIRM]).apply {
        this.isEnabled = !confirm
        this.addActionListener {
            windowAncestor?.dispose()
            okAction()
        }
    }

    val cancel = JButton(Strings[STR_CANCEL]).apply {
        this.addActionListener {
            windowAncestor?.dispose()
        }
    }

    JOptionPane.showOptionDialog(
        parent,
        if (confirm)
            JCheckBox(message.toString()).apply {
                this.addItemListener {
                    ok.isEnabled = it.stateChange == ItemEvent.SELECTED
                }
            }
        else message,
        title,
        JOptionPane.NO_OPTION,
        JOptionPane.WARNING_MESSAGE,
        null,
        arrayOf(ok, cancel),
        ok
    )
}

@OptIn(ExperimentalContracts::class)
inline fun <T : Container> T.applyAndAppendTo(
    parent: Container,
    constraints: Any? = null,
    block: T.() -> Unit
): T {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }

    this.apply(block)
    constraints?.let {
        parent.add(this, constraints)
    } ?: parent.add(this)
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