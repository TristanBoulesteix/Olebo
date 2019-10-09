package jdr.exia

import jdr.exia.controller.HomeFrameController
import javax.swing.SwingUtilities
import javax.swing.UIManager

fun main() {
    SwingUtilities.invokeLater {
        UIManager.setLookAndFeel(
            UIManager.getSystemLookAndFeelClassName()
        )
        HomeFrameController.frame.isVisible = true
    }
}
