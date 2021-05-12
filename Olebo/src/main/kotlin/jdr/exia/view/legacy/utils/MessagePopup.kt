package jdr.exia.view.legacy.utils

import jdr.exia.localization.*
import java.awt.Component
import java.awt.event.ItemEvent
import javax.swing.JButton
import javax.swing.JCheckBox
import javax.swing.JOptionPane

enum class MessageType(val title: String, val OptionPaneType: Int) {
    INFO(StringLocale[STR_INFO], JOptionPane.INFORMATION_MESSAGE),
    WARNING(StringLocale[STR_WARNING], JOptionPane.WARNING_MESSAGE),
    ERROR(StringLocale[STR_ERROR], JOptionPane.INFORMATION_MESSAGE)
}

/**
 * Show a popup with a message
 */
fun showMessage(message: String, parent: Component? = null, messageType: MessageType = MessageType.INFO) =
    JOptionPane.showMessageDialog(
        parent,
        message,
        messageType.title,
        messageType.OptionPaneType
    )

inline fun showConfirmMessage(
    parent: Component? = null,
    message: Any,
    title: String,
    confirm: Boolean = false,
    crossinline okAction: () -> Unit
) {
    val ok = JButton(StringLocale[STR_CONFIRM]).apply {
        this.isEnabled = !confirm
        this.addActionListener {
            windowAncestor?.dispose()
            okAction()
        }
    }

    val cancel = JButton(StringLocale[STR_CANCEL]).apply {
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