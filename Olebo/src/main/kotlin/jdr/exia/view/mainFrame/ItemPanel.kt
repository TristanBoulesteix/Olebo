package jdr.exia.view.mainFrame

import jdr.exia.controller.ViewManager
import jdr.exia.model.element.Blueprint
import jdr.exia.model.element.Type
import java.awt.*
import java.io.File
import javax.imageio.ImageIO
import javax.swing.*

/**
 * This panel is intended to contain the entire list of items that the Game master can use
 * */
class ItemPanel : JPanel() {
    companion object {
        private val dimensionElement = Dimension(Int.MAX_VALUE, 40)
    }

    private val itemsView = JPanel().apply {
        this.layout = BoxLayout(this, BoxLayout.Y_AXIS)
    }

    init {
        this.layout = BorderLayout()
        this.add(JTextField("Rechercher"), BorderLayout.NORTH)
        this.add(JScrollPane(itemsView), BorderLayout.CENTER)
        reloadContent()
    }

    private fun reloadContent() {
        with(itemsView) {
            this.add(CustomTitlePanel("Objets").apply { this.isEnabled = false })

            ViewManager.items.filter { it.type == Type.OBJECT.type }.forEach {
                this.add(CustomPanel(it))
            }
        }
    }

    private class CustomTitlePanel(name: String) : JTextField(name) {
        init {
            this.maximumSize = dimensionElement
            this.disabledTextColor = Color.BLACK
            this.background = Color.YELLOW
            this.font = Font("Arial", Font.BOLD, 14)
        }
    }

    private class CustomPanel(element: Blueprint) : JPanel() {
        init {
            this.maximumSize = dimensionElement
            this.layout = BoxLayout(this, BoxLayout.X_AXIS)

            val label = JLabel().apply {
                this.size = Dimension(10, 40)
                val icon = ImageIO.read(File(element.sprite)).getScaledInstance(this.width, this.height, Image.SCALE_SMOOTH)
                this.icon = ImageIcon(icon)
            }

            this.add(label)
            this.add(JTextField(element.name).apply {
                this.isEnabled = false
                this.disabledTextColor = Color.BLACK
            })
        }
    }
}
