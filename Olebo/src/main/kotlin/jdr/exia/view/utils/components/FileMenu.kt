package jdr.exia.view.utils.components

import jdr.exia.VERSION
import jdr.exia.model.dao.Settings
import jdr.exia.view.utils.CTRL
import jdr.exia.view.utils.applyAndAppendTo
import java.awt.Component
import java.awt.event.ItemEvent
import java.awt.event.KeyEvent
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import javax.swing.*
import javax.swing.filechooser.FileNameExtensionFilter


class FileMenu : JMenu("Ficher") {
    private companion object {
        const val SCREENSHOT = "Prendre une capture d'écran"
    }

    init {
        JMenuItem(SCREENSHOT).applyAndAppendTo(this) {
            this.addActionListener {
                val parent = SwingUtilities.getWindowAncestor(this@FileMenu)
                JFileChooser().apply {
                    this.dialogTitle = SCREENSHOT
                    this.fileFilter = FileNameExtensionFilter("Image PNG", "png")
                    if (this.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
                        val fileToSave = if (this.selectedFile.extension == "png")
                            this.selectedFile
                        else
                            File("${this.selectedFile.parentFile.absolutePath}${File.separator}${this.selectedFile.nameWithoutExtension}.png")

                        val saveImg = {
                            ImageIO.write(getScreenShot(parent), "png", fileToSave)
                        }

                        if(fileToSave.exists()) {
                            val result = JOptionPane.showConfirmDialog(null, "Ce fichier existe déjà, voulez-vous le remplacer ?", "Enregister sous", JOptionPane.YES_NO_OPTION)
                            if(result == JOptionPane.YES_OPTION) saveImg()
                        } else saveImg()
                    }
                }
            }

            this.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_P, CTRL)
        }

        JCheckBoxMenuItem("Mises à jour automatiques").applyAndAppendTo(this) {
            this.isSelected = Settings.autoUpdate
            this.addItemListener {
                Settings.autoUpdate = it.stateChange == ItemEvent.SELECTED
            }
        }

        this.addSeparator()

        JMenuItem("A propos").applyAndAppendTo(this) {
            this.addActionListener {
                JOptionPane.showMessageDialog(null,
                        "Olebo - Version de l'application : $VERSION - Version de la base de données : ${Settings.databaseVersion}",
                        "A propos",
                        JOptionPane.INFORMATION_MESSAGE)
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