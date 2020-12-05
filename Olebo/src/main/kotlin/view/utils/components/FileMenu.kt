package view.utils.components

import OLEBO_VERSION
import model.dao.option.Settings
import model.dao.ZipError
import model.dao.internationalisation.*
import model.dao.loadOleboZipData
import model.dao.zipOleboDirectory
import utils.Result
import view.frames.OptionDialog
import view.frames.home.HomeFrame
import view.frames.rpg.MasterFrame
import view.frames.rpg.PlayerFrame
import view.utils.*
import java.awt.Component
import java.awt.Frame
import java.awt.Window
import java.awt.event.KeyEvent
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import javax.swing.*
import javax.swing.filechooser.FileNameExtensionFilter


class FileMenu : JMenu(Strings[STR_FILES]) {
    init {
        JMenuItem("${Strings[STR_EXPORT_DATA]} (ALPHA)").applyAndAppendTo(this) {
            this.addActionListener {
                val parent = SwingUtilities.getWindowAncestor(this@FileMenu)
                val extension = "olebo"
                JFileChooser().apply {
                    this.dialogTitle = Strings[STR_EXPORT_DATA]
                    this.fileFilter = FileNameExtensionFilter(Strings[STR_OLEBO_FILE], extension)
                    if (this.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
                        val fileToSave = if (this.selectedFile.extension == extension)
                            this.selectedFile
                        else
                            File("${this.selectedFile.parentFile.absolutePath}${File.separator}${this.selectedFile.nameWithoutExtension}.$extension")

                        if (fileToSave.exists()) {
                            val result = JOptionPane.showConfirmDialog(
                                null,
                                Strings[ST_FILE_ALREADY_EXISTS],
                                Strings[STR_SAVE_AS],
                                JOptionPane.YES_NO_OPTION
                            )
                            if (result == JOptionPane.YES_OPTION) zipOleboDirectory(fileToSave)
                        } else zipOleboDirectory(fileToSave)
                    }
                }
            }
        }

        JMenuItem("${Strings[STR_IMPORT_DATA]} (ALPHA)").applyAndAppendTo(this) {
            this.addActionListener {
                val parent = SwingUtilities.getWindowAncestor(this@FileMenu)

                showConfirmMessage(parent, Strings[ST_WARNING_CONFIG_RESET], Strings[STR_IMPORT_DATA]) {
                    JFileChooser().apply {
                        this.dialogTitle = Strings[STR_IMPORT_DATA]
                        this.fileFilter = FileNameExtensionFilter(Strings[STR_OLEBO_FILE], "olebo")
                        if (this.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
                            if (!this.selectedFile.isDirectory && this.selectedFile.exists()) {
                                val result = loadOleboZipData(this.selectedFile)
                                if (result is Result.Success) {
                                    showPopup(Strings[ST_CONFIGURATION_IMPORTED], parent)
                                    MasterFrame.isVisible = false
                                    PlayerFrame.hide()
                                    Frame.getFrames().forEach(Window::dispose)
                                    HomeFrame().isVisible = true
                                } else showPopup(
                                    when (result.value) {
                                        ZipError.DATABASE_HIGHER -> Strings[ST_WARNING_PREVIOUS_VERSION_FILE]
                                        ZipError.MISSING_FILES -> Strings[ST_WARNING_MISSING_CONF_FILES]
                                        else -> "${Strings[ST_UNKNOWN_ERROR]} ${Strings[ST_FILE_MAY_BE_CORRUPTED]}"
                                    }, parent, true
                                )
                            }
                        }
                    }
                }
            }
        }

        this.addSeparator()

        JMenuItem("Options").applyAndAppendTo(this) {
            this.addActionListener {
                OptionDialog(this@FileMenu.windowAncestor).isVisible = true
            }
        }

        JMenuItem(Strings[STR_TAKE_SCREENSHOT]).applyAndAppendTo(this) {
            this.addActionListener {
                val parent = this@FileMenu.windowAncestor
                JFileChooser().apply {
                    this.dialogTitle = Strings[STR_TAKE_SCREENSHOT]
                    this.fileFilter = FileNameExtensionFilter("Image PNG", "png")
                    if (this.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
                        val fileToSave = if (this.selectedFile.extension == "png")
                            this.selectedFile
                        else
                            File("${this.selectedFile.parentFile.absolutePath}${File.separator}${this.selectedFile.nameWithoutExtension}.png")

                        val saveImg = {
                            ImageIO.write(getScreenShot(parent), "png", fileToSave)
                        }

                        if (fileToSave.exists()) {
                            val result = JOptionPane.showConfirmDialog(
                                null,
                                Strings[ST_FILE_ALREADY_EXISTS],
                                Strings[STR_SAVE_AS],
                                JOptionPane.YES_NO_OPTION
                            )
                            if (result == JOptionPane.YES_OPTION) saveImg()
                        } else saveImg()
                    }
                }
            }

            this.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_P, CTRL)
        }

        JMenuItem(Strings[STR_ABOUT]).applyAndAppendTo(this) {
            this.addActionListener {
                JOptionPane.showMessageDialog(
                    null,
                    "Olebo - ${Strings[STR_APP_VERSION]} $OLEBO_VERSION - ${Strings[STR_DATABASE_VERSION]} ${Settings.databaseVersion}",
                    Strings[STR_ABOUT],
                    JOptionPane.INFORMATION_MESSAGE
                )
            }
            this.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0)
        }
    }

    private fun getScreenShot(component: Component): BufferedImage {
        val image = BufferedImage(
            component.bounds.width,
            component.bounds.height,
            BufferedImage.TYPE_INT_RGB
        )

        component.paint(image.graphics)
        return image
    }
}