package view.utils

import model.internationalisation.STR_CANCEL
import model.internationalisation.STR_CONFIRM
import model.internationalisation.STR_WARNING
import model.internationalisation.Strings
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

fun showConfirmMessage(parent: Component? = null, message: String, title: String, okAction: () -> Unit) {
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

fun <T : Container> T.applyAndAppendTo(
    parent: Container,
    constraints: GridBagConstraints? = null,
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