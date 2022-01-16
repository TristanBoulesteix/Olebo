package jdr.exia

import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.Tray
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberTrayState
import jdr.exia.localization.*
import jdr.exia.model.dao.option.Settings
import jdr.exia.update.*
import jdr.exia.view.HomeWindow
import jdr.exia.view.MasterWindow
import jdr.exia.view.ui.OleboTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

const val OLEBO_VERSION_NAME = "0.1.4"

/**
 * This code must be unique between releases and must be incremented for each one
 */
const val OLEBO_VERSION_CODE = 5

fun main() = application {
    // Initialize translations
    LaunchedEffect(Unit) {
        StringLocale(Settings::activeLanguage)
    }

    // Initialize themes
    OleboTheme {
        // Manage update
        val trayState = rememberTrayState()

        var release by remember { mutableStateOf<Release?>(null) }
        var updateChecked by remember { mutableStateOf(false) }

        if (!updateChecked || release != null) {
            val trayHint by remember {
                derivedStateOf { if (release == null) StringLocale[ST_OLEBO_SEARCH_FOR_UPDATE] else StringLocale[STR_PREPARE_UPDATE] }
            }

            Tray(icon = UpdateTrayIcon, state = trayState, tooltip = trayHint)
        }

        LaunchedEffect(Unit) {
            checkForUpdate().onSuccess { release = it }.onFailure {
                if (it is Exception)
                    it.printStackTrace()
            }
            updateChecked = true
        }

        release?.let {
            UpdateUI(release = it, notify = trayState::sendNotification, hideTray = { release = null })
        }

        // Start of the main UI if automatic update are disabled
        if (!Settings.autoUpdate || (Settings.autoUpdate && updateChecked && (release == null))) {
            var changelogs: String? by remember { mutableStateOf(null) }

            LaunchedEffect(Unit) {
                launch(Dispatchers.IO) {
                    if (Settings.wasJustUpdated) {
                        changelogs = getChangelogs()
                    }
                }
            }

            MainUI()

            if (changelogs != null && Settings.wasJustUpdated) {
                ChangelogsDialog(changelogs!!, onClose = { Settings.wasJustUpdated = false })
            }
        }
    }
}

@Composable
fun ApplicationScope.MainUI() {
    var windowState by remember { mutableStateOf<WindowState>(WindowState.HomeWindow) }

    when (val currentWindow = windowState) {
        is WindowState.HomeWindow -> HomeWindow(startAct = { windowState = WindowState.MasterWindow(it) })
        is WindowState.MasterWindow -> MasterWindow(
            act = currentWindow.act,
            onExit = { windowState = WindowState.HomeWindow }
        )
    }
}

/**
 * Icon of the tray used to show update status
 *
 * TODO : Add a real icon
 */
private object UpdateTrayIcon : Painter() {
    override val intrinsicSize = Size(256f, 256f)

    override fun DrawScope.onDraw() {
        drawOval(Color(0xFFFFA500))
    }
}