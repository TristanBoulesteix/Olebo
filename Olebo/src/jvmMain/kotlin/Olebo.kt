package jdr.exia

import androidx.compose.runtime.*
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.Notification
import fr.olebo.utils.onNotSuccess
import fr.olebo.utils.onSuccess
import fr.olebo.utils.onThrow
import jdr.exia.localization.*
import jdr.exia.model.dao.option.Preferences
import jdr.exia.model.dao.option.Settings
import jdr.exia.update.*
import jdr.exia.view.ui.LocalTrayManager
import jdr.exia.view.ui.TrayManager
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

private const val MAX_UPDATE_ATTEMPT = 1u

fun main(vararg args: String) = oleboApplication {
    val applicationCoroutineScope = rememberCoroutineScope()

    var splashScreenVisible by remember { mutableStateOf(true) }

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
                    checkForUpdate()
                    TODO()
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
    }
}

@Immutable
private data class FailedUpdateData(val versionCode: Int, val attempts: UInt)

suspend fun SplashScreenActionScope.autoUpdate(
    release: Release,
    trayManager: TrayManager,
    onDone: () -> Unit,
    showUpdateDialog: (UInt) -> Unit
) {
    val attemptNumber = Preferences.getNumberOfUpdateAttemptForVersion(release.versionId)

    val strUpdateFailed = StringLocale[ST_UPDATE_FAILED]

    if (attemptNumber > MAX_UPDATE_ATTEMPT) {
        setStatus(strUpdateFailed, isError = true)
        trayManager.trayHint = strUpdateFailed

        trayManager.sendNotification(
            Notification(
                StringLocale[STR_ERROR],
                StringLocale[ST_INT1_INT2_UPDATE_FAILED_FOR_X_ATTEMPT, release.versionId, attemptNumber]
            )
        )

        showUpdateDialog(attemptNumber)
    } else {
        trayManager.sendNotification(
            Notification(
                StringLocale[STR_UPDATE_AVAILABLE],
                StringLocale[ST_UPDATE_OLEBO_RESTART]
            )
        )
        downloadUpdateAndExit(
            onFinishDownload = onDone,
            onDownloadFailure = {
                setStatus(strUpdateFailed, isError = true)
                trayManager.sendNotification(Notification(StringLocale[STR_ERROR], strUpdateFailed))
                it.printStackTrace()
            },
            versionCode = release.versionId
        )
    }
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
            checkForUpdate().onSuccess { release = it }.onThrow {
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