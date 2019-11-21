package jdr.exia

import jdr.exia.view.homeFrame.HomeFrame
import javax.swing.SwingUtilities
import javax.swing.UIManager

fun main() {
    checkForUpdate()

    SwingUtilities.invokeLater {
        UIManager.setLookAndFeel(
            UIManager.getSystemLookAndFeelClassName()
        )

        HomeFrame().isVisible = true
    }
}

fun checkForUpdate() = Thread(Runnable {

}).run()
