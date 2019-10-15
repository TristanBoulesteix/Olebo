package jdr.exia

import jdr.exia.view.homeFrame.HomeFrame
import javax.swing.SwingUtilities
import javax.swing.UIManager

fun main() {
    SwingUtilities.invokeLater {
        UIManager.setLookAndFeel(
            UIManager.getSystemLookAndFeelClassName()
        )
        HomeFrame().isVisible = true
        //ViewManager
    }
}