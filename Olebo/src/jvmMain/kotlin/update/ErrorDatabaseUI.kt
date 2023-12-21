package jdr.exia.update

import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Notification
import fr.olebo.utils.getOrNull
import fr.olebo.utils.isFailure
import jdr.exia.localization.*
import jdr.exia.model.dao.reset
import jdr.exia.model.dao.restart
import jdr.exia.view.component.contentListRow.ContentButtonBuilder
import jdr.exia.view.component.dialog.ConfirmMessage
import jdr.exia.view.component.dialog.MessageDialog
import jdr.exia.view.ui.LocalTrayManager
import jdr.exia.view.ui.oleboApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.system.exitProcess

fun showErrorDatabaseUI(versionCode: Int): Boolean {
    var shouldContinue = false

    oleboApplication {
        val scope = rememberCoroutineScope()

        val trayManager = LocalTrayManager.current

        var updateVersionCode by remember { mutableStateOf<Int?>(null) }

        var searchForUpdateDone by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            launch(Dispatchers.IO) {
                trayManager.trayHint = StringLocale[ST_OLEBO_SEARCH_FOR_UPDATE]

                var release = checkForUpdate(versionCode)

                if (release.isFailure) {
                    release = checkForUpdate()
                }

                updateVersionCode = release.getOrNull()?.versionId

                searchForUpdateDone = true

                if (updateVersionCode != null) {
                    trayManager.trayHint = StringLocale[STR_UPDATE_AVAILABLE]
                }
            }
        }

        var errorDialogIsVisible by remember { mutableStateOf(true) }

        var confirmReset by remember { mutableStateOf(false) }

        val updateText = when {
            !searchForUpdateDone -> StringLocale[ST_OLEBO_SEARCH_FOR_UPDATE]
            updateVersionCode != null -> StringLocale[STR_UPDATE]
            else -> StringLocale[STR_NO_UPDATE_AVAILABLE]
        }

        MessageDialog(
            title = StringLocale[STR_DB_VERSION_MISMATCH],
            message = StringLocale[ST_DB_VERSION_MISMATCH_MESSAGE],
            width = 500.dp,
            height = 400.dp,
            onCloseRequest = {},
            visible = errorDialogIsVisible,
            buttonBuilders = {
                ContentButtonBuilder(updateText, enabled = updateVersionCode != null) {
                    scope.launch {
                        trayManager.trayHint = StringLocale[STR_PREPARE_UPDATE]

                        errorDialogIsVisible = false

                        trayManager.sendNotification(
                            Notification(
                                StringLocale[STR_UPDATE_AVAILABLE],
                                StringLocale[ST_UPDATE_OLEBO_RESTART]
                            )
                        )

                        downloadUpdateAndExit(
                            onFinishDownload = { exitProcess(0) },
                            onDownloadFailure = { error("Unable to update") },
                            versionCode = updateVersionCode!!
                        )
                    }
                }

                ContentButtonBuilder(StringLocale[STR_RESET]) {
                    confirmReset = true
                    errorDialogIsVisible = false
                }

                ContentButtonBuilder(StringLocale[STR_CONTINUE]) {
                    shouldContinue = true
                    exitApplication()
                }

                ContentButtonBuilder(StringLocale[STR_EXIT]) {
                    exitProcess(0)
                }
            }
        )

        if (confirmReset) {
            ConfirmMessage(
                title = StringLocale[STR_RESET],
                message = StringLocale[ST_WARNING_CONFIG_RESET],
                onCloseRequest = {
                    confirmReset = false
                    errorDialogIsVisible = true
                },
                onConfirm = {
                    reset()
                    restart()
                }
            )
        }
    }

    return shouldContinue
}