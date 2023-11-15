package jdr.exia

import androidx.compose.runtime.*
import androidx.compose.ui.window.ApplicationScope
import jdr.exia.localization.*
import jdr.exia.model.dao.option.Preferences
import jdr.exia.model.dao.option.Settings
import jdr.exia.update.*
import jdr.exia.view.ui.LocalTrayManager
import jdr.exia.view.ui.oleboApplication
import jdr.exia.view.window.screen.HomeWindow
import jdr.exia.view.window.screen.MasterWindow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

const val OLEBO_VERSION_NAME = "0.1.5"

/**
 * This code must be unique between releases and must be incremented for each one
 */
const val OLEBO_VERSION_CODE = 6

fun main(vararg args: String) = oleboApplication {
    var splashScreenVisible by remember { mutableStateOf(true) }

    val trayManager = LocalTrayManager.current

    LaunchedEffect(Unit) {
        trayManager.trayHint = "Olebo is running"
    }

    if (splashScreenVisible) {
        SplashScreen(onDone = { splashScreenVisible = false }) {
            // Initialize database and localization
            setStatus(10f, "Initialization database and localization")
            StringLocale(Settings::activeLanguage)

            // Check dev mode
            setStatus(20f, "Checking developer mode")
            if ("-dev" in args) {
                DeveloperModeManager.toggle()
            }

            // Check for update
            if (Settings.autoUpdate) {
                setStatus(50f, "Checking for updates")
                checkForUpdate().onSuccess {
                    setStatus(60f, "Updating to ${it.versionName}")
                    autoUpdate(it)
                }.onFailure {
                    setStatus(100f, "Failed to check for updates...", isError = true)
                    if (it is Exception) it.printStackTrace()
                }
            } else {

            }
        }
    } else {
        // Main content
        exitApplication()
    }
}

/**
 * Automatically updates the software with the given release.
 *
 * @param release The release object containing the details of the software update.
 */
private suspend fun autoUpdate(release: Release) {

}

suspend fun temp(args: String) {
    // Initialize translations and database
    StringLocale(Settings::activeLanguage)

    if ("-dev" in args) {
        DeveloperModeManager.toggle()
    }

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
    var windowState by remember { mutableStateOf<WindowState>(HomeWindow) }

    when (val currentWindow = windowState) {
        is HomeWindow -> HomeWindow(startAct = { windowState = MasterWindow(it) })
        is MasterWindow -> MasterWindow(act = currentWindow.act,
            onExit = { windowState = HomeWindow })
    }
}