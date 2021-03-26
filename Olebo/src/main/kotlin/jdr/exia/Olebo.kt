package jdr.exia

import jdr.exia.localization.Strings
import jdr.exia.model.dao.option.Settings
import jdr.exia.updater.checkForUpdate
import jdr.exia.view.compose.showHomeWindow
import javax.swing.SwingUtilities
import javax.swing.UIManager

const val OLEBO_VERSION = "1.9.0-BETA"

fun main() {
    SwingUtilities.invokeLater {
        Strings(Settings.Companion::activeLanguage)

        checkForUpdate()

        UIManager.setLookAndFeel(
            UIManager.getSystemLookAndFeelClassName()
        )

        showHomeWindow()

/*        with(HomeFrame()) {
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
        }*/
    }
}

