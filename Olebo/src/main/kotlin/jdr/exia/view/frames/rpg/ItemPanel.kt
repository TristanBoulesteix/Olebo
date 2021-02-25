package jdr.exia.view.frames.rpg

import jdr.exia.localization.*
import jdr.exia.model.element.Blueprint
import jdr.exia.model.element.Type
import jdr.exia.utils.forElse
import jdr.exia.view.frames.Reloadable
import jdr.exia.view.utils.components.templates.PlaceholderTextField
import jdr.exia.view.utils.event.ClickListener
import jdr.exia.view.utils.event.addClickListener
import jdr.exia.view.utils.event.addTextChangedListener
import jdr.exia.viewModel.ViewManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.awt.*
import java.io.File
import javax.imageio.ImageIO
import javax.swing.*


/**
 * This panel is intended to contain the entire list of items that the Game master can use (and it does)
 */
class ItemPanel : JPanel(), Reloadable {
    companion object {
        private val dimensionElement = Dimension(Int.MAX_VALUE, 40)
    }

    private val itemsView = JPanel().apply {
        this.layout = BoxLayout(this, BoxLayout.Y_AXIS)
    }

    /**
     * Search field to find a specific blueprint
     */
    private val searchField = PlaceholderTextField(Strings[STR_SEARCH])

    /**
     * Constraint applied to the content to show after a result
     */
    private var searchConstraint = ""

    init {
        this.layout = BorderLayout()
        this.searchField.addTextChangedListener {
            searchConstraint = searchField.text
            reload()
        }
        this.add(searchField, BorderLayout.NORTH)
        this.add(JScrollPane(itemsView), BorderLayout.CENTER)
    }

    override fun reload() {
        transaction {
            with(itemsView) {
                // Remove previous components
                this.removeAll()
                this.updateUI()

                // Object list
                this.add(CustomTitlePanel(Strings[STR_OBJECTS]).apply { this.isEnabled = false })

                ViewManager.items.filter {
                    it.type.typeElement == Type.OBJECT && (searchConstraint.isEmpty() || it.name.contains(
                        searchConstraint,
                        true
                    ))
                }.forElse {
                    this.add(CustomPanel(it))
                } ?: this.add(EmptyField())

                // PJ list
                this.add(CustomTitlePanel(Strings[STR_PC]).apply { this.isEnabled = false })

                ViewManager.items.filter {
                    it.type.typeElement == Type.PJ && (searchConstraint.isEmpty() || it.name.contains(
                        searchConstraint,
                        true
                    ))
                }.forElse {
                    this.add(CustomPanel(it))
                } ?: this.add(EmptyField())

                // PNJ list
                this.add(CustomTitlePanel(Strings[STR_NPC]).apply { this.isEnabled = false })

                ViewManager.items.filter {
                    it.type.typeElement == Type.PNJ && (searchConstraint.isEmpty() || it.name.contains(
                        searchConstraint,
                        true
                    ))
                }.forElse {
                    this.add(CustomPanel(it))
                } ?: this.add(EmptyField())

                // Basic elements list
                this.add(CustomTitlePanel(Strings[STR_BASE_ELEMENT_PLR]).apply { this.isEnabled = false })

                ViewManager.items.filter {
                    it.type.typeElement == Type.BASIC && (searchConstraint.isEmpty() || it.realName.contains(
                        searchConstraint,
                        true
                    ))
                }.forElse {
                    this.add(CustomPanel(it))
                } ?: this.add(EmptyField())
            }
        }
    }

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
        private val eventListener = ClickListener { ViewManager.addToken(element) }

        init {
            this.maximumSize = dimensionElement
            this.layout = BoxLayout(this, BoxLayout.X_AXIS)
            this.isFocusable = true

            val label = JLabel().apply {
                this.size = Dimension(40, 40)

                val imageIo = if (element.type.typeElement == Type.BASIC)
                    ImageIO.read(ItemPanel::class.java.classLoader.getResourceAsStream("sprites/${element.sprite}"))
                else ImageIO.read(File(element.sprite))

                this.icon = ImageIcon(imageIo.getScaledInstance(this.width, this.height, Image.SCALE_SMOOTH))
            }

            this.add(label)
            this.add(JTextField(element.realName).apply {
                this.isEnabled = false
                this.disabledTextColor = Color.BLACK
                this.isFocusable = false
                this.addClickListener(eventListener)
            })

            this.addClickListener(eventListener)
        }
    }

    /**
     * Class showing "Aucun élément" as JTextField
     */
    class EmptyField : JTextField(Strings[STR_NO_ELEMENT]) {
        init {
            this.isEnabled = false
            this.maximumSize = dimensionElement
        }
    }
}
