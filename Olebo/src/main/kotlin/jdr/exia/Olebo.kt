package jdr.exia

import jdr.exia.localization.Strings
import jdr.exia.model.dao.option.Settings
import jdr.exia.updater.checkForUpdate
import jdr.exia.updater.currentChangelogs
import jdr.exia.view.frames.home.HomeFrame
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.swing.JOptionPane
import javax.swing.SwingUtilities
import javax.swing.UIManager

const val OLEBO_VERSION = "1.8.0-BETA"

fun main() {
    SwingUtilities.invokeLater {
        Strings(Settings.Companion::activeLanguage)

        checkForUpdate()

        UIManager.setLookAndFeel(
            UIManager.getSystemLookAndFeelClassName()
        )

        with(HomeFrame()) {
            isVisible = true
            GlobalScope.launch(Dispatchers.IO) {
                if (Settings.wasJustUpdated) {
                    currentChangelogs.takeIf { !it.isNullOrBlank() }?.let {
                        JOptionPane.showMessageDialog(
                            this@with,
                            it,
                            "Changelogs",
                            JOptionPane.INFORMATION_MESSAGE
                        )

                        Settings.wasJustUpdated = false
                    }
                }
            }
        }
    }
}