package jdr.exia

import androidx.compose.runtime.*
import androidx.compose.ui.window.ApplicationScope
import jdr.exia.localization.*
import jdr.exia.model.dao.option.Preferences
import jdr.exia.model.dao.option.Settings
import jdr.exia.update.*
import jdr.exia.view.HomeWindow
import jdr.exia.view.MasterWindow
import jdr.exia.view.ui.LocalTrayManager
import jdr.exia.view.ui.oleboApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

const val OLEBO_VERSION_NAME = "0.1.5"

/**
 * This code must be unique between releases and must be incremented for each one
 */
const val OLEBO_VERSION_CODE = 6

fun main() {
    // Initialize translations and database
    StringLocale(Settings::activeLanguage)

    oleboApplication {
        // Manage update
        var release by remember { mutableStateOf<Release?>(null) }
        var updateChecked by remember { mutableStateOf(false) }

        val trayManager = LocalTrayManager.current

        LaunchedEffect(release, updateChecked) {
            trayManager.trayHint = when {
                updateChecked -> StringLocale[STR_OLEBO_IS_RUNNING]
                release == null -> StringLocale[ST_OLEBO_SEARCH_FOR_UPDATE]
                else -> StringLocale[STR_PREPARE_UPDATE]
            }
        }

        LaunchedEffect(Unit) {
            checkForUpdate().onSuccess { release = it }.onFailure {
                if (it is Exception) it.printStackTrace()
            }
            updateChecked = true
        }

        release?.let {
            UpdateUI(release = it, notify = trayManager::sendNotification, hideTray = { release = null })
        }

        // Start the main UI if automatic updates are disabled
        if (!Settings.autoUpdate || (Settings.autoUpdate && updateChecked && release == null)) {
            var changelogs: String? by remember { mutableStateOf(null) }

            LaunchedEffect(Unit) {
                launch(Dispatchers.IO) {
                    if (Preferences.wasJustUpdated) {
                        changelogs = getChangelogs()
                    }
                }
            }

            MainUI()

            if (changelogs != null && Preferences.wasJustUpdated) {
                ChangelogsDialog(changelogs!!, onClose = { Preferences.versionUpdatedTo = -1 })
            }
        }
    }
}

@Composable
fun ApplicationScope.MainUI() {
    var windowState by remember { mutableStateOf<WindowState>(WindowState.HomeWindow) }

    when (val currentWindow = windowState) {
        is WindowState.HomeWindow -> HomeWindow(startAct = { windowState = WindowState.MasterWindow(it) })
        is WindowState.MasterWindow -> MasterWindow(act = currentWindow.act,
            onExit = { windowState = WindowState.HomeWindow })
    }
}