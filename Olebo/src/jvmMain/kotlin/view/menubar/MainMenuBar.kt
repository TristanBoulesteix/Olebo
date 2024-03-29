package jdr.exia.view.menubar

import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyShortcut
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.MenuBar
import androidx.compose.ui.window.MenuBarScope
import jdr.exia.OLEBO_VERSION_CODE
import jdr.exia.OLEBO_VERSION_NAME
import jdr.exia.localization.*
import jdr.exia.main
import jdr.exia.model.dao.DAO
import jdr.exia.model.dao.loadOleboZipData
import jdr.exia.model.dao.option.ThemeMode
import jdr.exia.model.dao.zipOleboDirectory
import jdr.exia.update.ChangelogsDialog
import jdr.exia.update.getChangelogs
import jdr.exia.view.SettingsDialog
import jdr.exia.view.WindowStateManager
import jdr.exia.view.element.dialog.ConfirmMessage
import jdr.exia.view.element.dialog.LoadingDialog
import jdr.exia.view.element.dialog.MessageDialog
import jdr.exia.view.tools.windowAncestor
import jdr.exia.view.ui.LocalTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.awt.Desktop
import java.io.File
import java.io.InputStreamReader
import java.net.URI
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
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
    val longProcessRunningMessage = remember { mutableStateOf("") }

    if (longProcessRunningMessage.value.isNotBlank())
        LoadingDialog(reasonMessage = longProcessRunningMessage.value)

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

    Item(text = StringLocale[STR_IMPORT_DATA]) {
        confirmedImport = true
    }

    Separator()

    var isSettingsDialogVisible by remember { mutableStateOf(false) }

    if (isSettingsDialogVisible) {
        SettingsDialog {
            isSettingsDialogVisible = false
            WindowStateManager.currentFocusedWindowScope?.triggerSettingsChange()
        }
    }

    Item(text = StringLocale[STR_OPTIONS]) {
        isSettingsDialogVisible = true
    }

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

    var changelogs by remember { mutableStateOf("") }

    Item(text = StringLocale[STR_RELEASE_NOTES]) {
        changelogs = getChangelogs() ?: ""
    }

    if (changelogs.isNotBlank()) {
        ChangelogsDialog(changelogs) {
            changelogs = ""
        }
    }

    lateinit var desktop: Desktop

    Item(
        text = StringLocale[STR_CONTACT_DEVELOPERS],
        enabled = Desktop.isDesktopSupported() && Desktop.getDesktop().also { desktop = it }
            .isSupported(Desktop.Action.MAIL)
    ) {
        val body = StringLocale.getLocalizedResource("contact/body", "txt", ::main.javaClass.classLoader)?.reader()
            ?.use(InputStreamReader::readText)?.let { URLEncoder.encode(it, StandardCharsets.UTF_8).replace("+", "%20") } ?: ""

        val mailto = URI("mailto:contact.olebo@tb-lab.fr?subject=Bug%20report%20%2F%20feature%20request&body=$body")
        desktop.mail(mailto)
    }

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

@OptIn(DelicateCoroutinesApi::class)
private inline fun MutableState<String>.executeBlockingProcess(
    reasonMessage: String,
    crossinline process: suspend CoroutineScope.() -> Unit
) = GlobalScope.launch {
    value = reasonMessage
    process()
    value = ""
}