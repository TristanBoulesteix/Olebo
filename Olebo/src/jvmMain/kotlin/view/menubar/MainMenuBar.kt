package jdr.exia.view.menubar

import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyShortcut
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.MenuBar
import androidx.compose.ui.window.MenuBarScope
import androidx.compose.ui.window.MenuScope
import jdr.exia.DeveloperModeManager
import jdr.exia.OLEBO_VERSION_CODE
import jdr.exia.OLEBO_VERSION_NAME
import jdr.exia.localization.*
import jdr.exia.main
import jdr.exia.model.dao.DAO
import jdr.exia.model.dao.loadOleboZipData
import jdr.exia.model.dao.option.ThemeMode
import jdr.exia.model.dao.zipOleboDirectory
import jdr.exia.service.sendMailToDevelopers
import jdr.exia.update.ChangelogsDialog
import jdr.exia.update.getChangelogs
import jdr.exia.view.component.dialog.ConfirmMessage
import jdr.exia.view.component.dialog.LoadingDialog
import jdr.exia.view.component.dialog.MessageDialog
import jdr.exia.view.tools.windowAncestor
import jdr.exia.view.ui.LocalTheme
import jdr.exia.view.windows.LocalWindow
import jdr.exia.view.windows.options.DeveloperSettingsDialog
import jdr.exia.view.windows.options.SettingsDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.InputStreamReader
import javax.swing.JFileChooser
import javax.swing.JOptionPane
import javax.swing.filechooser.FileNameExtensionFilter

@Composable
fun FrameWindowScope.MainMenuBar(exitApplication: () -> Unit) = MenuBar {
    MainMenus(exitApplication)
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MenuBarScope.MainMenus(exitApplication: () -> Unit) = Menu(text = StringLocale[STR_FILES], mnemonic = 'f') {
    // Long process running message
    // Show a loading dialog when the message is not empty
    val longProcessRunningMessage = remember { mutableStateOf("") }

    if (longProcessRunningMessage.value.isNotBlank())
        LoadingDialog(reasonMessage = longProcessRunningMessage.value)

    // Export Olebo data to a file
    Item(text = StringLocale[STR_EXPORT_DATA]) {
        val extension = "olebo"
        JFileChooser().apply {
            this.dialogTitle = StringLocale[STR_EXPORT_DATA]
            this.fileFilter = FileNameExtensionFilter(StringLocale[STR_OLEBO_FILE], extension)
            if (this.showSaveDialog(this.windowAncestor) == JFileChooser.APPROVE_OPTION) {
                val fileToSave = if (this.selectedFile.extension == extension)
                    this.selectedFile
                else
                    File("${this.selectedFile.parentFile.absolutePath}${File.separator}${this.selectedFile.nameWithoutExtension}.$extension")

                if (!fileToSave.exists() || JOptionPane.showConfirmDialog(
                        null,
                        StringLocale[ST_FILE_ALREADY_EXISTS],
                        StringLocale[STR_SAVE_AS],
                        JOptionPane.YES_NO_OPTION
                    ) == JOptionPane.YES_OPTION
                ) {
                    longProcessRunningMessage.executeBlockingProcess(StringLocale[STR_EXPORT_DATA]) {
                        zipOleboDirectory(fileToSave)
                    }
                }
            }
        }
    }

    var confirmedImport by remember { mutableStateOf(false) }

    var infoMessage: String? by remember { mutableStateOf(null) }

    if (confirmedImport) {
        ConfirmMessage(
            message = StringLocale[ST_WARNING_CONFIG_RESET],
            title = StringLocale[STR_IMPORT_DATA],
            onCloseRequest = { confirmedImport = false }) {
            JFileChooser().apply {
                this.dialogTitle = StringLocale[STR_IMPORT_DATA]
                this.fileFilter = FileNameExtensionFilter(StringLocale[STR_OLEBO_FILE], "olebo")
                if (this.showOpenDialog(this.windowAncestor) == JFileChooser.APPROVE_OPTION && !this.selectedFile.isDirectory && this.selectedFile.exists()) {
                    longProcessRunningMessage.executeBlockingProcess(StringLocale[STR_IMPORT_DATA]) {
                        loadOleboZipData(selectedFile).onSuccess {
                            if (it == null) {
                                infoMessage = StringLocale[ST_CONFIGURATION_IMPORTED]

                                // close all composable windows and then restart the main function
                                exitApplication()

                                launch {
                                    DAO.refreshDatabase()
                                    main()
                                }
                            } else {
                                infoMessage = it
                            }
                        }.onFailure {
                            infoMessage = "${StringLocale[ST_UNKNOWN_ERROR]} ${StringLocale[ST_FILE_MAY_BE_CORRUPTED]}"
                        }
                    }
                }
            }
        }
    }

    infoMessage?.let {
        MessageDialog("Info", message = it, onCloseRequest = { infoMessage = null })
    }

    // Import data from file
    Item(text = StringLocale[STR_IMPORT_DATA]) {
        confirmedImport = true
    }

    Separator()

    // Open settings
    var isSettingsDialogVisible by remember { mutableStateOf(false) }

    if (isSettingsDialogVisible) {
        val currentWindow = LocalWindow.current

        SettingsDialog {
            isSettingsDialogVisible = false
            currentWindow?.triggerSettingsChanged()
        }
    }

    Item(text = StringLocale[STR_OPTIONS]) {
        isSettingsDialogVisible = true
    }

    // Developer mode settings
    if(DeveloperModeManager.enabledState.value) {
        DeveloperModeSettingsMenuItem()
    }

    // Dark / Light theme manager
    val oleboTheme = LocalTheme.current

    Menu(text = "${StringLocale[STR_THEME]} ${oleboTheme.themeMode}") {
        val themeModes = remember { ThemeMode.values().toList() }

        themeModes.forEach {
            RadioButtonItem("$it", selected = oleboTheme.themeMode == it) {
                oleboTheme.themeMode = it
            }
        }
    }

    Separator()

    // Change logs handler
    var changelogs by remember { mutableStateOf("") }

    Item(text = StringLocale[STR_RELEASE_NOTES]) {
        changelogs = getChangelogs() ?: ""
    }

    if (changelogs.isNotBlank()) {
        ChangelogsDialog(changelogs) {
            changelogs = ""
        }
    }

    // Contact developers menu
    val uriHandler = LocalUriHandler.current

    Item(text = StringLocale[STR_CONTACT_DEVELOPERS]) {
        val body = StringLocale.getLocalizedResource("contact/body", "txt", ::main.javaClass.classLoader)?.reader()
            ?.use(InputStreamReader::readText) ?: ""

        uriHandler.sendMailToDevelopers("Bug report / Feature request", body)
    }

    // About dialog
    var aboutDialogVisible by remember { mutableStateOf(false) }

    Item(text = StringLocale[STR_ABOUT], shortcut = KeyShortcut(Key.F1)) {
        aboutDialogVisible = true
    }

    if (aboutDialogVisible) {
        MessageDialog(
            title = StringLocale[STR_ABOUT],
            message = """
                Olebo - ${StringLocale[STR_APP_VERSION]} $OLEBO_VERSION_NAME - ${StringLocale[STR_VERSION_CODE]} $OLEBO_VERSION_CODE
                ${StringLocale[ST_DEVELOPED_BY]}
            """.trimIndent(),
            onCloseRequest = { aboutDialogVisible = false },
            width = 500.dp,
            height = 215.dp,
            messageLineHeight = 45.sp
        )
    }
}

@Composable
private fun MenuScope.DeveloperModeSettingsMenuItem() {
    var isOptionDialogVisible by remember { mutableStateOf(false) }

    Item("Options mode développeur") {
        isOptionDialogVisible = true
    }

    if(isOptionDialogVisible) {
        DeveloperSettingsDialog { isOptionDialogVisible = false }
    }
}

@OptIn(DelicateCoroutinesApi::class)
private inline fun MutableState<String>.executeBlockingProcess(
    reasonMessage: String,
    crossinline process: suspend CoroutineScope.() -> Unit
) = GlobalScope.launch {
    value = reasonMessage
    process()
    value = ""
}