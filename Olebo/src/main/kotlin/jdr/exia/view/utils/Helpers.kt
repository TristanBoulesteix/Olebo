package jdr.exia.view.utils

import jdr.exia.model.element.Element
import jdr.exia.model.element.Type
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

fun Element?.isCharacter(): Boolean {
    return this != null && (this.type.typeElement == Type.PNJ || this.type.typeElement == Type.PJ)
}