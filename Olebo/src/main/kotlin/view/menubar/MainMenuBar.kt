package jdr.exia.view.menubar

import androidx.compose.runtime.*
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyShortcut
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.MenuBar
import androidx.compose.ui.window.MenuBarScope
import jdr.exia.OLEBO_VERSION_CODE
import jdr.exia.OLEBO_VERSION_NAME
import jdr.exia.localization.*
import jdr.exia.main
import jdr.exia.model.dao.loadOleboZipData
import jdr.exia.model.dao.zipOleboDirectory
import jdr.exia.view.OptionDialog
import jdr.exia.view.WindowStateManager
import jdr.exia.view.element.dialog.ConfirmMessage
import jdr.exia.view.tools.MessageType
import jdr.exia.view.tools.showMessage
import jdr.exia.view.tools.windowAncestor
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import javax.swing.JFileChooser
import javax.swing.JOptionPane
import javax.swing.filechooser.FileNameExtensionFilter

@Composable
fun FrameWindowScope.MainMenuBar(exitApplication: () -> Unit) = MenuBar {
    MainMenus(exitApplication)
}

@OptIn(DelicateCoroutinesApi::class, androidx.compose.ui.ExperimentalComposeUiApi::class)
@Composable
fun MenuBarScope.MainMenus(exitApplication: () -> Unit) = Menu(text = StringLocale[STR_FILES], mnemonic = 'f') {
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

                if (fileToSave.exists()) {
                    val result = JOptionPane.showConfirmDialog(
                        null,
                        StringLocale[ST_FILE_ALREADY_EXISTS],
                        StringLocale[STR_SAVE_AS],
                        JOptionPane.YES_NO_OPTION
                    )
                    if (result == JOptionPane.YES_OPTION) zipOleboDirectory(fileToSave)
                } else zipOleboDirectory(fileToSave)
            }
        }
    }

    var confirmedImport by remember { mutableStateOf(false) }

    if (confirmedImport) {
        ConfirmMessage(
            message = StringLocale[ST_WARNING_CONFIG_RESET],
            title = StringLocale[STR_IMPORT_DATA],
            onCloseRequest = { confirmedImport = false }) {
            JFileChooser().apply {
                this.dialogTitle = StringLocale[STR_IMPORT_DATA]
                this.fileFilter = FileNameExtensionFilter(StringLocale[STR_OLEBO_FILE], "olebo")
                if (this.showOpenDialog(this.windowAncestor) == JFileChooser.APPROVE_OPTION && !this.selectedFile.isDirectory && this.selectedFile.exists()) {
                    loadOleboZipData(this.selectedFile).onSuccess {
                        showMessage(
                            StringLocale[ST_CONFIGURATION_IMPORTED],
                            this.windowAncestor
                        )

                        // close all composable windows and then restart the main function
                        exitApplication()

                        GlobalScope.launch {
                            main()
                        }
                    }.onFailure {
                        showMessage(
                            if (it !is IllegalStateException) "${StringLocale[ST_UNKNOWN_ERROR]} ${StringLocale[ST_FILE_MAY_BE_CORRUPTED]}" else it.message
                                ?: "",
                            this.windowAncestor,
                            MessageType.ERROR
                        )
                    }
                }
            }
        }
    }

    Item(text = StringLocale[STR_IMPORT_DATA]) {
        confirmedImport = true
    }

    Separator()

    Item(text = StringLocale[STR_OPTIONS]) {
        OptionDialog(WindowStateManager.currentFocusedWindow).isVisible = true
        WindowStateManager.currentFocusedWindow?.repaint()
    }

    Item(text = StringLocale[STR_TAKE_SCREENSHOT], enabled = false, shortcut = KeyShortcut(Key.P, ctrl = true)) {
        /*        val parent = this@FileMenu.windowAncestor
        JFileChooser().apply {
            this.dialogTitle = StringLocale[STR_TAKE_SCREENSHOT]
            this.fileFilter = FileNameExtensionFilter("Image PNG", "png")
            if (this.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
                val fileToSave = if (this.selectedFile.extension == "png")
                    this.selectedFile
                else
                    File("${this.selectedFile.parentFile.absolutePath}${File.separator}${this.selectedFile.nameWithoutExtension}.png")

                val saveImg = {
                    if (parent != null)
                        ImageIO.write(getScreenShot(parent), "png", fileToSave)
                }

                if (fileToSave.exists()) {
                    val result = JOptionPane.showConfirmDialog(
                        null,
                        StringLocale[ST_FILE_ALREADY_EXISTS],
                        StringLocale[STR_SAVE_AS],
                        JOptionPane.YES_NO_OPTION
                    )
                    if (result == JOptionPane.YES_OPTION) saveImg()
                } else saveImg()
            }
        }*/
    }

    Item(text = StringLocale[STR_ABOUT], shortcut = KeyShortcut(Key.F1)) {
        JOptionPane.showMessageDialog(
            null,
            "Olebo - ${StringLocale[STR_APP_VERSION]} $OLEBO_VERSION_NAME - ${StringLocale[STR_VERSION_CODE]} $OLEBO_VERSION_CODE",
            StringLocale[STR_ABOUT],
            JOptionPane.INFORMATION_MESSAGE
        )
    }
}
