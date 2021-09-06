package jdr.exia.update

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import jdr.exia.MainUI
import jdr.exia.localization.*
import jdr.exia.model.dao.option.Settings
import jdr.exia.view.element.builder.ContentButtonBuilder
import jdr.exia.view.element.dialog.PromptDialog
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.delay

@Composable
fun ApplicationScope.UpdateUI(release: Release, notifify: (Notification) -> Unit, hideTray: () -> Unit) {
    val id = release.versionId

    if (Settings.autoUpdate) {
        val notification =
            rememberNotification(StringLocale[STR_UPDATE_AVAILABLE], StringLocale[ST_UPDATE_OLEBO_RESTART])

        var failedToUpdate by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            notifify(notification)
            delay(2_000)
            downloadAndExit(
                onExitSuccess = ::exitApplication,
                onDownloadFailure = {
                    failedToUpdate = true
                    notifify(Notification(StringLocale[STR_ERROR], StringLocale[ST_UPDATE_FAILED]))
                }
            )
        }

        if (failedToUpdate)
            MainUI()

    } else if (id.toString() != Settings.updateWarn) {
        PromptUpdate(versionCode = id, onUpdateRefused = hideTray)
    }
}

@Composable
private fun ApplicationScope.PromptUpdate(versionCode: Int, onUpdateRefused: () -> Unit) {
    var askForUpdateDialogIsVisible by remember { mutableStateOf(true) }
    var updateIsStarted by remember { mutableStateOf(false) }

    PromptDialog(
        visible = askForUpdateDialogIsVisible,
        onCloseRequest = { askForUpdateDialogIsVisible = false },
        title = StringLocale[STR_UPDATE_AVAILABLE],
        message = StringLocale[ST_NEW_VERSION_AVAILABLE],
        width = 600.dp,
        buttonBuilders = listOf(
            ContentButtonBuilder(content = StringLocale[STR_YES], onClick = {
                askForUpdateDialogIsVisible = false
                updateIsStarted = true
            }),
            ContentButtonBuilder(content = StringLocale[STR_NO], onClick = {
                askForUpdateDialogIsVisible = false
                onUpdateRefused()
            }),
            ContentButtonBuilder(content = StringLocale[ST_NEVER_ASK_UPDATE], onClick = {
                Settings.updateWarn = versionCode.toString()
                askForUpdateDialogIsVisible = false
                onUpdateRefused()
            })
        )
    )

    if (updateIsStarted) {
        InstallerDownloader(exitApplication = ::exitApplication)
    }
}

@OptIn(DelicateCoroutinesApi::class)
@Composable
private fun InstallerDownloader(exitApplication: () -> Unit) {
    var isVisible by remember { mutableStateOf(true) }

    if (isVisible) {
        val dialogState = rememberDialogState(height = 150.dp)
        var progress by remember { mutableStateOf(0f) }

        Dialog(title = StringLocale[STR_DOWNLOAD_UPDATE], onCloseRequest = {}, resizable = false, state = dialogState) {
            this.window.isModal = true

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize().padding(end = 10.dp)
            ) {
                val padding = Modifier.padding(horizontal = 10.dp)

                LinearProgressIndicator(modifier = Modifier.fillMaxWidth().then(padding), progress = progress)
                Text(StringLocale[STR_DOWNLOAD] + " $progress %", modifier = padding)
            }
        }

        LaunchedEffect(Unit) {
            downloadAndExit(
                onExitSuccess = exitApplication,
                onProgressUpdate = { progress = it.toFloat() },
                onDownloadSuccess = { isVisible = false })
        }
    }
}

