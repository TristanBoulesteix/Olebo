package jdr.exia.update

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.LocalContentColor
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.Notification
import androidx.compose.ui.window.rememberDialogState
import jdr.exia.SplashScreenActionScope
import jdr.exia.localization.*
import jdr.exia.model.dao.option.Preferences
import jdr.exia.model.dao.option.Settings
import jdr.exia.model.dao.restart
import jdr.exia.service.sendMailToDevelopers
import jdr.exia.view.component.contentListRow.ContentButtonBuilder
import jdr.exia.view.component.dialog.MessageDialog
import jdr.exia.view.tools.annotatedHyperlink
import jdr.exia.view.tools.appendBulletList
import jdr.exia.view.ui.LocalTrayManager
import jdr.exia.view.ui.TrayManager
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.awt.Dialog
import kotlin.system.exitProcess

private const val REPORT_ISSUE_TAG = "report"

private const val CONTACT_DEVS_TAG = "contact"

private const val MAX_UPDATE_ATTEMPT = 1u

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

@OptIn(DelicateCoroutinesApi::class)
@Composable
fun FailedAutoUpdateDialog(versionCode: Int, attemptNumber: UInt) {
    val trayManager = LocalTrayManager.current

    var isVisible by remember { mutableStateOf(true) }

    MessageDialog(
        title = StringLocale[STR_UPDATE_INSTALL_ERROR],
        width = 800.dp,
        height = 500.dp,
        onCloseRequest = { isVisible = false },
        visible = isVisible,
        buttonsBuilder = {
            ContentButtonBuilder(StringLocale[STR_OK]) {
                isVisible = false
            }

            ContentButtonBuilder(StringLocale[STR_RETRY_UPDATE]) {
                isVisible = false

                GlobalScope.launch {
                    trayManager.trayHint = StringLocale[STR_PREPARE_UPDATE]

                    trayManager.sendNotification(
                        Notification(
                            StringLocale[STR_PREPARE_UPDATE],
                            StringLocale[ST_UPDATE_OLEBO_RESTART]
                        )
                    )

                    downloadUpdateAndExit(
                        onFinishDownload = { exitProcess(0) },
                        onDownloadFailure = {
                            GlobalScope.launch {
                                trayManager.sendNotification(
                                    Notification(
                                        StringLocale[STR_ERROR],
                                        StringLocale[ST_UPDATE_FAILED]
                                    )
                                )

                                delay(2_000)

                                restart(-1)
                            }
                        },
                        versionCode = versionCode
                    )
                }
            }
        }
    ) {
        val uriHandler = LocalUriHandler.current

        val message = buildAnnotatedString {
            append(StringLocale[ST_INT1_INT2_UPDATE_FAILED_FOR_X_ATTEMPT, versionCode, attemptNumber])
            append("\n\n")
            append(StringLocale[STR_TRY_IF_PROBLEM_PERSISTS])
            append('\n')

            appendBulletList(
                AnnotatedString(StringLocale[STR_CHECK_INTERNET_FIREWALL]),
                annotatedHyperlink(
                    text = StringLocale[STR_CREATE_ISSUE_FOR_UPDATE],
                    message = "https://github.com/TristanBoulesteix/Olebo/issues/new",
                    tag = REPORT_ISSUE_TAG
                ),
                annotatedHyperlink(
                    text = StringLocale[STR_CONTACT_DEVELOPERS_FOR_UPDATE_FAILURE],
                    message = "Bug report",
                    tag = CONTACT_DEVS_TAG
                )
            )
        }

        ClickableText(
            text = message,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 5.dp),
            style = LocalTextStyle.current.copy(color = LocalContentColor.current),
            onClick = { position ->
                message.getStringAnnotations(position, position).firstOrNull()?.let {
                    if (it.tag == REPORT_ISSUE_TAG) {
                        uriHandler.openUri(it.item)
                    } else if (it.tag == CONTACT_DEVS_TAG) {
                        uriHandler.sendMailToDevelopers(it.item)
                    }
                }
            })
    }
}

@Composable
fun ApplicationScope.PromptUpdate(release: Release, onUpdateRefused: () -> Unit) {
    var askForUpdateDialogIsVisible by remember { mutableStateOf(true) }
    var updateIsStarted by remember { mutableStateOf(false) }

    MessageDialog(
        visible = askForUpdateDialogIsVisible,
        onCloseRequest = { askForUpdateDialogIsVisible = false },
        title = StringLocale[STR_UPDATE_AVAILABLE],
        message = StringLocale[ST_STR1_NEW_VERSION_AVAILABLE, release.versionName],
        width = 600.dp,
        buttonBuilders = {
            ContentButtonBuilder(content = StringLocale[STR_YES], onClick = {
                askForUpdateDialogIsVisible = false
                updateIsStarted = true
            })

            ContentButtonBuilder(content = StringLocale[STR_NO], onClick = {
                askForUpdateDialogIsVisible = false
                onUpdateRefused()
            })

            ContentButtonBuilder(content = StringLocale[ST_NEVER_ASK_UPDATE], onClick = {
                Settings.updateWarn = release.versionId.toString()
                askForUpdateDialogIsVisible = false
                onUpdateRefused()
            })
        }
    )

    if (updateIsStarted) {
        InstallerDownloader(exitApplication = ::exitApplication, versionCode = release.versionId)
    }
}

@Composable
private fun InstallerDownloader(versionCode: Int, exitApplication: () -> Unit) {
    var isVisible by remember { mutableStateOf(true) }

    if (isVisible) {
        val dialogState = rememberDialogState(height = 150.dp)
        var progress by remember { mutableFloatStateOf(0f) }

        DialogWindow(
            onCloseRequest = {},
            state = dialogState,
            title = StringLocale[STR_DOWNLOAD_UPDATE],
            resizable = false,
            content = {
                LaunchedEffect(Unit) { window.modalityType = Dialog.ModalityType.APPLICATION_MODAL }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize().padding(end = 10.dp)
                ) {
                    val padding = Modifier.padding(horizontal = 10.dp)

                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth().then(padding), progress = progress)
                    Text(StringLocale[STR_DOWNLOAD] + " $progress %", modifier = padding)
                }
            })

        LaunchedEffect(Unit) {
            downloadUpdateAndExit(
                versionCode = versionCode,
                onFinishDownload = exitApplication,
                onProgressUpdate = { progress = it.toFloat() },
                onDownloadSuccess = { isVisible = false }
            )
        }
    }
}

