package jdr.exia

import jdr.exia.localization.Strings
import jdr.exia.model.dao.option.Settings
import jdr.exia.updater.checkForUpdate
import jdr.exia.view.frames.home.HomeFrame
import javax.swing.SwingUtilities
import javax.swing.UIManager

const val OLEBO_VERSION = "1.8.0-BETA"

const val DEBUG = false

fun main() {
    SwingUtilities.invokeLater {
        Strings(Settings.Companion::activeLanguage)

        if (!DEBUG)
            checkForUpdate()
        UIManager.setLookAndFeel(
            UIManager.getSystemLookAndFeelClassName()
        )

        HomeFrame().isVisible = true
    }
}