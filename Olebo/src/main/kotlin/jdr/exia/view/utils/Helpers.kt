package jdr.exia.view.utils

import java.awt.Component
import javax.swing.JOptionPane

/**
 * Show a popup with a message
 */
fun showPopup(message: String, parent: Component? = null) = JOptionPane.showMessageDialog(
    parent,
    message,
    "Attention !",
    JOptionPane.INFORMATION_MESSAGE
)