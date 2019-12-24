package jdr.exia.view.utils

import java.awt.*
import javax.swing.JFrame
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

fun Window.setFullScreen() {
    val screenSize: Dimension = Toolkit.getDefaultToolkit().screenSize
    this.setSize(screenSize.width, screenSize.height)
    if(this is Frame) this.extendedState = JFrame.MAXIMIZED_BOTH
    //else if(this is JDialog)
}