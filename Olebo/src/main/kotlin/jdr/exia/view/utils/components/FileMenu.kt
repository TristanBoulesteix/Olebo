package jdr.exia.view.utils.components

import jdr.exia.view.utils.applyAndAppend
import java.awt.Component
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import javax.swing.JFileChooser
import javax.swing.JMenu
import javax.swing.JMenuItem
import javax.swing.SwingUtilities
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
    }

    private fun getScreenShot(component: Component): BufferedImage? {
        val image = BufferedImage(
                component.width,
                component.height,
                BufferedImage.TYPE_INT_RGB
        )

        component.paint(image.graphics)
        return image
    }
}