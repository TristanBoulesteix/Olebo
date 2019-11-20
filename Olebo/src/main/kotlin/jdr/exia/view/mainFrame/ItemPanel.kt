package jdr.exia.view.mainFrame

import jdr.exia.controller.ViewManager
import jdr.exia.model.dao.DAO
import jdr.exia.model.element.Blueprint
import jdr.exia.model.element.Type
import jdr.exia.view.utils.event.ClickListener
import org.jetbrains.exposed.sql.transactions.transaction
import java.awt.*
import java.awt.RenderingHints
import java.awt.event.MouseEvent
import java.io.File
import javax.imageio.ImageIO
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener


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

    /**
     * Search field to find a specific blueprint
     */
    private val searchField = object : JTextField() {
        override fun paintComponent(pG: Graphics) {
            super.paintComponent(pG)

            if(text.isNotEmpty()) return

            (pG as Graphics2D).apply {
                setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
                )
                color = disabledTextColor
                drawString(
                    "Rechercher", insets.left, pG.getFontMetrics()
                        .maxAscent + insets.top
                )
            }
        }
    }

    /**
     * Event which trigger when the search field is modified
     */
    private val changeEvent = object : DocumentListener {
        override fun changedUpdate(e: DocumentEvent?) = warn()

        override fun insertUpdate(e: DocumentEvent?) = warn()

        override fun removeUpdate(e: DocumentEvent?) = warn()

        fun warn() {
            searchConstraint = searchField.text
            reloadContent()
        }
    }

    /**
     * Constraint applied to the content to show after a result
     */
    private var searchConstraint = ""

    init {
        this.layout = BorderLayout()
        this.searchField.document.addDocumentListener(changeEvent)
        this.add(searchField, BorderLayout.NORTH)
        this.add(JScrollPane(itemsView), BorderLayout.CENTER)
    }

    /**
     * Reload content depending on searchConstraint
     */
    fun reloadContent() {
        transaction(DAO.database) {
            with(itemsView) {
                // Remove previous components
                this.removeAll()
                this.updateUI()

                // Object list
                this.add(CustomTitlePanel("Objets").apply { this.isEnabled = false })

                ViewManager.items.filter {
                    it.type.typeElement == Type.OBJECT && (searchConstraint.isEmpty() || it.name.contains(
                        searchConstraint
                    ))
                }.forElse {
                    this.add(CustomPanel(it))
                } ?: this.add(EmptyField())

                // PJ list
                this.add(CustomTitlePanel("PJ").apply { this.isEnabled = false })

                ViewManager.items.filter {
                    it.type.typeElement == Type.PJ && (searchConstraint.isEmpty() || it.name.contains(
                        searchConstraint
                    ))
                }.forElse {
                    this.add(CustomPanel(it))
                } ?: this.add(EmptyField())

                // PNJ list
                this.add(CustomTitlePanel("PNJ").apply { this.isEnabled = false })

                ViewManager.items.filter {
                    it.type.typeElement == Type.PNJ && (searchConstraint.isEmpty() || it.name.contains(
                        searchConstraint
                    ))
                }.forElse {
                    this.add(CustomPanel(it))
                } ?: this.add(EmptyField())
            }
        }
    }

    private fun <T> List<T>.forElse(block: (T) -> Unit) = if (isEmpty()) null else forEach(block)

    /**
     * Name of the component
     *
     * @param name The name of the component as String
     */
    private class CustomTitlePanel(name: String) : JTextField(name) {
        init {
            this.maximumSize = dimensionElement
            this.disabledTextColor = Color.BLACK
            this.background = Color.YELLOW
            this.font = Font("Arial", Font.BOLD, 14)
        }
    }

    /**
     * Icon of the component
     */
    private class CustomPanel(element: Blueprint) : JPanel() {
        private val eventListener = object : ClickListener {
            override fun mouseClicked(e: MouseEvent?) {
                ViewManager.addToken(element)
            }
        }

        init {
            this.maximumSize = dimensionElement
            this.layout = BoxLayout(this, BoxLayout.X_AXIS)
            this.isFocusable = true

            val label = JLabel().apply {
                this.size = Dimension(40, 40)
                val icon =
                    ImageIO.read(File(element.sprite)).getScaledInstance(this.width, this.height, Image.SCALE_SMOOTH)
                this.icon = ImageIcon(icon)
            }

            this.add(label)
            this.add(JTextField(element.name).apply {
                this.isEnabled = false
                this.disabledTextColor = Color.BLACK
                this.isFocusable = false
                this.addMouseListener(eventListener)
            })

            this.addMouseListener(eventListener)
        }
    }

    /**
     * Class showing "Aucun élément" as JTextField
     */
    class EmptyField : JTextField("Aucun élément") {
        init {
            this.isEnabled = false
            this.maximumSize = dimensionElement
        }
    }
}
