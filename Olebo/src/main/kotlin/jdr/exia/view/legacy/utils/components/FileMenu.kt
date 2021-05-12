package jdr.exia.view.legacy.utils.components

import jdr.exia.OLEBO_VERSION
import jdr.exia.localization.*
import jdr.exia.model.dao.loadOleboZipData
import jdr.exia.model.dao.option.Settings
import jdr.exia.model.dao.zipOleboDirectory
import jdr.exia.model.utils.Result
import jdr.exia.view.HomeFrame
import jdr.exia.view.legacy.frames.OptionDialog
import jdr.exia.view.legacy.frames.rpg.MasterFrame
import jdr.exia.view.legacy.frames.rpg.PlayerFrame
import jdr.exia.view.legacy.utils.*
import java.awt.Component
import java.awt.Frame
import java.awt.Window
import java.awt.event.KeyEvent
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import javax.swing.*
import javax.swing.filechooser.FileNameExtensionFilter


class FileMenu : JMenu(StringLocale[STR_FILES]) {
    init {
        JMenuItem(StringLocale[STR_EXPORT_DATA]).applyAndAddTo(this) {
            this.addActionListener {
                val extension = "olebo"
                JFileChooser().apply {
                    this.dialogTitle = StringLocale[STR_EXPORT_DATA]
                    this.fileFilter = FileNameExtensionFilter(StringLocale[STR_OLEBO_FILE], extension)
                    if (this.showSaveDialog(this@FileMenu.windowAncestor) == JFileChooser.APPROVE_OPTION) {
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
        }

        JMenuItem(StringLocale[STR_IMPORT_DATA]).applyAndAddTo(this) {
            this.addActionListener {
                showConfirmMessage(
                    this@FileMenu.windowAncestor,
                    StringLocale[ST_WARNING_CONFIG_RESET],
                    StringLocale[STR_IMPORT_DATA],
                    confirm = true
                ) {
                    JFileChooser().apply {
                        this.dialogTitle = StringLocale[STR_IMPORT_DATA]
                        this.fileFilter = FileNameExtensionFilter(StringLocale[STR_OLEBO_FILE], "olebo")
                        if (this.showOpenDialog(this@FileMenu.windowAncestor) == JFileChooser.APPROVE_OPTION) {
                            if (!this.selectedFile.isDirectory && this.selectedFile.exists()) {
                                when (val result = loadOleboZipData(this.selectedFile)) {
                                    is Result.Success -> {
                                        showMessage(
                                            StringLocale[ST_CONFIGURATION_IMPORTED],
                                            this@FileMenu.windowAncestor
                                        )
                                        MasterFrame.isVisible = false
                                        PlayerFrame.hide()
                                        Frame.getFrames().forEach(Window::dispose)
                                        HomeFrame().isVisible = true
                                    }
                                    is Result.Failure -> {
                                        showMessage(
                                            if (result.causeUnknown) "${StringLocale[ST_UNKNOWN_ERROR]} ${StringLocale[ST_FILE_MAY_BE_CORRUPTED]}" else result.message,
                                            this@FileMenu.windowAncestor,
                                            MessageType.ERROR
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        this.addSeparator()

        JMenuItem(StringLocale[STR_OPTIONS]).applyAndAddTo(this) {
            this.addActionListener {
                OptionDialog(this@FileMenu.windowAncestor).isVisible = true
            }
        }

        JMenuItem(StringLocale[STR_TAKE_SCREENSHOT]).applyAndAddTo(this) {
            this.isEnabled = false // TODO: Make it work with Jetpack Compose
            this.addActionListener {
                val parent = this@FileMenu.windowAncestor
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
                }
            }

            this.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_P, CTRL)
        }

        JMenuItem(StringLocale[STR_ABOUT]).applyAndAddTo(this) {
            this.addActionListener {
                JOptionPane.showMessageDialog(
                    null,
                    "Olebo - ${StringLocale[STR_APP_VERSION]} $OLEBO_VERSION - ${StringLocale[STR_DATABASE_VERSION]} ${Settings.databaseVersion}",
                    StringLocale[STR_ABOUT],
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