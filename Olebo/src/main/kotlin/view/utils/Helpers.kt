package view.utils

import java.awt.Component
import java.awt.GridBagConstraints
import java.awt.event.ItemEvent
import javax.swing.*

/**
 * Show a popup with a message
 */
fun showPopup(message: String, parent: Component? = null) = JOptionPane.showMessageDialog(
        parent,
        message,
        "Attention !",
        JOptionPane.INFORMATION_MESSAGE
)

fun showConfirmMessage(parent: JComponent? = null, message: String, title: String, okAction: () -> Unit) {
    val ok = JButton("Confirmer").apply {
        this.isEnabled = false
        this.addActionListener {
            okAction()
            SwingUtilities.getWindowAncestor(this).isVisible = false
        }
    }

    val cancel = JButton("Annuler").apply {
        this.addActionListener {
            SwingUtilities.getWindowAncestor(this).isVisible = false
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

fun <T : JComponent> T.applyAndAppendTo(parent: JComponent, constraints: GridBagConstraints? = null, block: T.() -> Unit): T {
    this.apply(block)
    constraints?.let {
        parent.add(this, constraints)
    } ?: parent.add(this)
    return this
}

fun <T> Collection<T>.forElse(block: (T) -> Unit) = if (isEmpty()) null else forEach(block)