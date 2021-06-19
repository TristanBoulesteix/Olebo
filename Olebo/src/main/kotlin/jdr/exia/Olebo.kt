package jdr.exia

import getReleaseManagerAsync
import jdr.exia.localization.StringLocale
import jdr.exia.model.dao.option.Settings
import jdr.exia.update.currentChangelogs
import jdr.exia.view.HomeWindow
import kotlinx.coroutines.*
import kotlinx.coroutines.swing.Swing
import javax.swing.JOptionPane
import javax.swing.UIManager

const val OLEBO_VERSION = "0.1.0"

@OptIn(DelicateCoroutinesApi::class)
suspend fun main(): Unit = coroutineScope {
    launch(Dispatchers.Swing) {
        StringLocale(Settings.Companion::activeLanguage)

        GlobalScope.launch(Dispatchers.IO) {
            manageUpdate()
        }

        UIManager.setLookAndFeel(
            UIManager.getSystemLookAndFeelClassName()
        )

        HomeWindow().isVisible = true

        launch {
            if (Settings.wasJustUpdated) {
                currentChangelogs.takeIf { !it.isNullOrBlank() }?.let {
                    JOptionPane.showMessageDialog(
                        null,
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

private suspend fun manageUpdate() = coroutineScope {
    val updateManager = getReleaseManagerAsync(OLEBO_VERSION).await()

    if(updateManager.hasUpdateAvailable) {

    }
}
