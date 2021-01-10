package jdr.exia.view.utils

import jdr.exia.localization.STR_CANCEL
import jdr.exia.localization.STR_CONFIRM
import jdr.exia.localization.STR_WARNING
import jdr.exia.localization.Strings
import jdr.exia.utils.MessageException
import java.awt.*
import java.awt.event.ItemEvent
import javax.swing.JButton
import javax.swing.JCheckBox
import javax.swing.JOptionPane
import javax.swing.SwingUtilities

/**
 * Show a popup with a message
 */
fun showPopup(message: String, parent: Component? = null, isError: Boolean = false) = JOptionPane.showMessageDialog(
    parent,
    message,
    Strings[STR_WARNING],
    if (!isError) JOptionPane.INFORMATION_MESSAGE else JOptionPane.ERROR_MESSAGE
)

inline fun showConfirmMessage(parent: Component? = null, message: String, title: String, crossinline okAction: () -> Unit) {
    val ok = JButton(Strings[STR_CONFIRM]).apply {
        this.isEnabled = false
        this.addActionListener {
            SwingUtilities.getWindowAncestor(this)?.dispose()
            okAction()
        }
    }

    val cancel = JButton(Strings[STR_CANCEL]).apply {
        this.addActionListener {
            SwingUtilities.getWindowAncestor(this)?.dispose()
        }
    }

    JOptionPane.showOptionDialog(
        parent,
        JCheckBox(message).apply {
            this.addItemListener {
                ok.isEnabled = it.stateChange == ItemEvent.SELECTED
            }
        },
        title,
        JOptionPane.NO_OPTION,
        JOptionPane.WARNING_MESSAGE,
        null,
        arrayOf(ok, cancel),
        ok
    )
}

inline fun <T : Container> T.applyAndAppendTo(
    parent: Container,
    constraints: Any? = null,
    block: T.() -> Unit
): T {
    this.apply(block)
    constraints?.let {
        parent.add(this, constraints)
    } ?: parent.add(this)
    return this
}

private val Dimension.area
    get() = width * height

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

val Component.windowAncestor: Window
    get() = SwingUtilities.getWindowAncestor(this) ?: throw MessageException("")

fun Graphics.fillCircleWithCenterCoordinates(x: Int, y: Int, radius: Int) =
    fillOval(x - radius, y - radius, radius * 2, radius * 2)

fun Graphics2D.drawCircleWithCenterCoordinates(x: Int, y: Int, radius: Int) {
    val previousStroke = this.stroke
    this.stroke = BasicStroke(3F)
    this.drawOval(x - radius, y - radius, radius * 2, radius * 2)
    this.stroke = previousStroke
}