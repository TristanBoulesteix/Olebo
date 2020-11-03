package jdr.exia

import jdr.exia.utils.HttpClientUpdater
import jdr.exia.view.homeFrame.HomeFrame
import javax.swing.SwingUtilities
import javax.swing.UIManager

const val VERSION = "1.1.1-BETA"

fun main() {
    HttpClientUpdater.checkForUpdate()

    SwingUtilities.invokeLater {
        UIManager.setLookAndFeel(
            UIManager.getSystemLookAndFeelClassName()
        )

        HomeFrame().isVisible = true
    }
}
