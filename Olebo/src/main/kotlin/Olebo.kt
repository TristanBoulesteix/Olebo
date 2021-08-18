package jdr.exia

import androidx.compose.desktop.DesktopMaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.window.application
import jdr.exia.localization.StringLocale
import jdr.exia.model.dao.option.Settings
import jdr.exia.update.Release
import jdr.exia.update.checkForUpdate
import jdr.exia.update.legacy.currentChangeLogs
import jdr.exia.view.HomeWindow
import jdr.exia.view.MasterWindow
import jdr.exia.view.UpdateUI
import jdr.exia.view.ui.OleboTheme
import kotlinx.coroutines.launch
import javax.swing.JOptionPane
import javax.swing.UIManager

const val OLEBO_VERSION_NAME = "0.1.0"

/**
 * This code must be unique between releases and must be incremented for each one
 */
const val OLEBO_VERSION_CODE = 1

var release by mutableStateOf<Release?>(null)

fun main(): Unit = application {
    // Set look and feel for Swing (less mandatory with compose)
    UIManager.setLookAndFeel(
        UIManager.getSystemLookAndFeelClassName()
    )

    // Initialize translations
    StringLocale(Settings.Companion::activeLanguage)

    // Initialize themes
    OleboTheme {
        DesktopMaterialTheme {
            // Manage update
            LaunchedEffect(Unit) {
                release = checkForUpdate()
            }

            release?.let {
                UpdateUI(it)
            }

            // Start of the main UI
            var windowState by remember { mutableStateOf<WindowState>(WindowState.HomeWindow) }

            when (val currentWindow = windowState) {
                is WindowState.HomeWindow -> HomeWindow(startAct = { windowState = WindowState.MasterWindow(it) })
                is WindowState.MasterWindow -> {
                    MasterWindow(
                        act = currentWindow.act,
                        onExit = { windowState = WindowState.HomeWindow }
                    )
                }
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
