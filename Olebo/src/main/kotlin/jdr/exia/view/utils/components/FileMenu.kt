package jdr.exia.view.utils.components

import jdr.exia.VERSION
import jdr.exia.model.dao.Settings
import jdr.exia.view.utils.applyAndAppend
import java.awt.Component
import java.awt.event.ItemEvent
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import javax.swing.*
import javax.swing.filechooser.FileNameExtensionFilter


class FileMenu : JMenu("Ficher") {
    companion object {
        const val SCREENSHOT = "Prendre une capture d'écran"
    }

    init {
        JMenuItem(SCREENSHOT).applyAndAppend(this) {
            this.addActionListener {
                val parent = SwingUtilities.getWindowAncestor(this@FileMenu)
                JFileChooser().apply {
                    this.dialogTitle = SCREENSHOT
                    this.fileFilter = FileNameExtensionFilter("Image PNG", "png")
                    if (this.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
                        ImageIO.write(getScreenShot(parent), "png", if (this.selectedFile.extension == "png")
                            this.selectedFile
                        else
                            File("${this.selectedFile.parentFile.absolutePath}${File.separator}${this.selectedFile.nameWithoutExtension}.png"))
                    }
                }
            }
        }

        JCheckBoxMenuItem("Mises à jour automatiques").applyAndAppend(this) {
            this.isSelected = Settings.autoUpdate
            this.addItemListener {
                Settings.autoUpdate = it.stateChange == ItemEvent.SELECTED
            }
        }

        this.addSeparator()

        JMenuItem("A propos").applyAndAppend(this) {
            this.addActionListener {
                JOptionPane.showMessageDialog(null,
                        "Olebo - Version de l'application : $VERSION - Version de la base de données : ${Settings.databaseVersion}",
                        "A propos",
                        JOptionPane.INFORMATION_MESSAGE)
            }
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