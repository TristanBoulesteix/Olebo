package jdr.exia.view.utils

import java.awt.Component
import javax.swing.JOptionPane

fun showPopup(message: String, parent: Component? = null) = JOptionPane.showMessageDialog(
    parent,
    message,
    "Attention !",
    JOptionPane.INFORMATION_MESSAGE
)