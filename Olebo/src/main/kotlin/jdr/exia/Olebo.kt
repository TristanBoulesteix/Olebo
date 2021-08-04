package jdr.exia

import androidx.compose.desktop.DesktopMaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.window.application
import jdr.exia.localization.*
import jdr.exia.model.act.Act
import jdr.exia.model.dao.option.Settings
import jdr.exia.update.currentChangeLogs
import jdr.exia.update.getUpdaterForCurrentOsAsync
import jdr.exia.view.HomeWindow
import jdr.exia.view.MasterWindow
import jdr.exia.view.ui.OleboTheme
import kotlinx.coroutines.*
import javax.swing.JOptionPane
import javax.swing.UIManager
import kotlin.system.exitProcess

const val OLEBO_VERSION = "0.1.0"

sealed class WindowState {
    object HomeWindow : WindowState()

    class MasterWindow(val act: Act) : WindowState()
}

@OptIn(DelicateCoroutinesApi::class)
fun main(): Unit = application {
    StringLocale(Settings.Companion::activeLanguage)

    GlobalScope.launch(Dispatchers.IO) {
        manageUpdate()
    }

    UIManager.setLookAndFeel(
        UIManager.getSystemLookAndFeelClassName()
    )

    OleboTheme {
        DesktopMaterialTheme {
            var windowState by remember { mutableStateOf<WindowState>(WindowState.HomeWindow) }

            when (val currentWindow = windowState) {
                is WindowState.HomeWindow -> HomeWindow(startAct = { windowState = WindowState.MasterWindow(it) })
                is WindowState.MasterWindow -> {
                    MasterWindow(
                        act = currentWindow.act,
                        onExit = { windowState = WindowState.HomeWindow }
                    )
                }
                else -> exitApplication()
            }
        }
    }

    rememberCoroutineScope().launch {
        if (Settings.wasJustUpdated) {
            currentChangeLogs.takeIf { !it.isNullOrBlank() }?.let {
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

private suspend fun manageUpdate() = coroutineScope {
    val updater = getUpdaterForCurrentOsAsync(OLEBO_VERSION).await()

    // If there is un updater available, it means that there is a new version available
    if (updater != null) {
        if (Settings.autoUpdate) {
            Settings.wasJustUpdated = true
            updater.startUpdate(Settings::autoUpdate)
        } else if (Settings.updateWarn != updater.versionName) {
            withContext(Dispatchers.Main) {
                val result = JOptionPane.showOptionDialog(
                    null,
                    StringLocale[ST_NEW_VERSION_AVAILABLE],
                    StringLocale[STR_UPDATE_AVAILABLE],
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    arrayOf(StringLocale[STR_YES], StringLocale[STR_NO], StringLocale[ST_NEVER_ASK_UPDATE]),
                    StringLocale[STR_NO]
                )

                if (result == JOptionPane.YES_OPTION) {
                    JOptionPane.showMessageDialog(
                        null,
                        StringLocale[ST_UPDATE_OLEBO_RESTART],
                        StringLocale[STR_PREPARE_UPDATE],
                        JOptionPane.INFORMATION_MESSAGE
                    )
                    Settings.wasJustUpdated = true
                    updater.startUpdate { true }
                    exitProcess(0)
                } else if (result == JOptionPane.CANCEL_OPTION) {
                    Settings.updateWarn = updater.versionName
                }
            }
        }
    }
}
