package jdr.exia

import jdr.exia.utils.checkForUpdate
import jdr.exia.view.homeFrame.HomeFrame
import javax.swing.SwingUtilities
import javax.swing.UIManager

const val VERSION = "1.1.1-BETA"

const val DEBUG = true

fun main() {
    if (!DEBUG)
        checkForUpdate()
    SwingUtilities.invokeLater {
        UIManager.setLookAndFeel(
                UIManager.getSystemLookAndFeelClassName()
        )

        HomeFrame().isVisible = true
    }
}
