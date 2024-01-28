package jdr.exia

import androidx.compose.runtime.*
import androidx.compose.ui.window.ApplicationScope
import fr.olebo.utils.onNotSuccess
import fr.olebo.utils.onSuccess
import jdr.exia.localization.*
import jdr.exia.model.dao.option.Preferences
import jdr.exia.model.dao.option.Settings
import jdr.exia.update.*
import jdr.exia.view.ui.LocalTrayManager
import jdr.exia.view.ui.oleboApplication
import jdr.exia.view.window.screen.HomeWindow
import jdr.exia.view.window.screen.MasterWindow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

const val OLEBO_VERSION_NAME = "0.1.5"

/**
 * This code must be unique between releases and must be incremented for each one
 */
const val OLEBO_VERSION_CODE = 6

fun main(vararg args: String) = oleboApplication {
    val applicationCoroutineScope = rememberCoroutineScope()

    var splashScreenVisible by remember { mutableStateOf(true) }

    var manualUpdate by remember { mutableStateOf<Release?>(null) }

    val trayManager = LocalTrayManager.current

    LaunchedEffect(Unit) {
        trayManager.trayHint = "Olebo is running"
    }

    if (splashScreenVisible) {
        var failedUpdateData: FailedUpdateData? by remember { mutableStateOf(null) }

        SplashScreen(onDone = { splashScreenVisible = false }) {
            // Initialize database and localization
            setStatus(10f, "Initialization database and localization")
            withContext(Dispatchers.Default) { StringLocale(Settings::activeLanguage) }

            // Check dev mode
            setStatus(20f, "Checking developer mode")
            if ("-dev" in args) {
                DeveloperModeManager.toggle()
            }

            // Check for update
            if (Settings.autoUpdate) {
                setStatus(50f, "Checking for updates")
                trayManager.trayHint = StringLocale[ST_OLEBO_SEARCH_FOR_UPDATE]
                checkForUpdate().onSuccess { release ->
                    setStatus(60f, "Updating to ${release.versionName}")
                    autoUpdate(
                        release = release,
                        trayManager = trayManager,
                        onDone = {
                            setStatus(90f, "Installation of Olebo v.${release.versionName}. The app will restart.")
                            exitApplication()
                        },
                        showUpdateDialog = { failedUpdateData = FailedUpdateData(release.versionId, it) }
                    )
                }.onNotSuccess { _, exception ->
                    if (exception != null) {
                        setStatus(90f, "Failed to check for updates...", isError = true)
                        exception.printStackTrace()
                    } else {
                        trayManager.trayHint = StringLocale[STR_OLEBO_IS_RUNNING]
                    }
                }
            } else {
                // Manual update
                applicationCoroutineScope.launch {
                    checkForUpdate().onSuccess { manualUpdate = it }
                }
            }

            setStatus(100f, "Starting Olebo")
            delay(2000)
        }

        failedUpdateData?.let {
            FailedAutoUpdateDialog(it.versionCode, it.attempts)
        }
    } else {
        MainUI()

        Changelog()

        manualUpdate?.let {
            PromptUpdate(release = it, onUpdateRefused = { manualUpdate = null })
        }
    }
}

@Immutable
private data class FailedUpdateData(val versionCode: Int, val attempts: UInt)

@Composable
private fun Changelog() {
    var changelogs: String? by remember { mutableStateOf(null) }

    LaunchedEffect(Unit) {
        launch(Dispatchers.IO) {
            if (Preferences.wasJustUpdated) {
                changelogs = getChangelogs()
            }
        }
    }

    if (changelogs != null && Preferences.wasJustUpdated) {
        ChangelogsDialog(changelogs!!, onClose = { Preferences.versionUpdatedTo = -1 })
    }
}

@Composable
fun ApplicationScope.MainUI() {
    var windowState by remember { mutableStateOf<WindowState>(HomeWindow) }

    when (val currentWindow = windowState) {
        is HomeWindow -> HomeWindow(startAct = { windowState = MasterWindow(it) })
        is MasterWindow -> {
            MasterWindow(act = currentWindow.act, onExit = { windowState = HomeWindow })
        }
    }
}