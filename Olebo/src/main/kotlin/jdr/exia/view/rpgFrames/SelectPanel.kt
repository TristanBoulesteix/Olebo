package jdr.exia.view.rpgFrames

import jdr.exia.model.dao.DAO
import jdr.exia.model.element.Element
import jdr.exia.model.element.Size
import jdr.exia.model.utils.isCharacter
import jdr.exia.view.utils.BACKGROUND_COLOR_SELECT_PANEL
import jdr.exia.view.utils.DEFAULT_BORDER_SIZE
import jdr.exia.view.utils.applyAndAppendTo
import jdr.exia.view.utils.components.SlideStats
import jdr.exia.viewModel.ViewManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics
import java.awt.GridLayout
import javax.swing.*
import javax.swing.border.EmptyBorder


/**
 * Contains all this info regarding the item selected by the Game Master.
 * This is a singleton.
 */
object SelectPanel : JPanel() {
    var selectedElement: Element? = null
        set(value) {
            field = value

            if (selectedElement != null) {
                sizeCombo.selectedItem = selectedElement!!.size.name
            } else {
                sizeCombo.selectedItem = null
            }
        }

    private val slidePanel: JPanel

    private var lifeSlide = SlideStats(true)

    private var manaSlide = SlideStats(false)

    private val nameLabel = object : JLabel("Nom") {
        init {
            horizontalTextPosition = CENTER
            border = EmptyBorder(20, 0, 0, 0)
        }

        override fun setText(text: String?) {
            if (text == null) {
                this.isEnabled = false
                super.setText("Nom")
            } else {
                this.isEnabled = true
                super.setText(text)
            }
        }
    }

    private val hpAmount = JLabel("X/Y").apply {
        border = EmptyBorder(0, 20, 10, 0)
    }

    private val hpField = JTextField().apply {
        preferredSize = Dimension(50, 25)
    }

    private val hpButton = JButton("Ajouter PV").apply {
        //adds current indicated amount of HP to the char's total
        preferredSize = Dimension(100, 40)
        this.addActionListener {
            if (selectedElement.isCharacter()) {
                transaction(DAO.database) { selectedElement!!.currentHealth += checkTextValue(hpField.text) }
            }
            hpField.text = ""
            MasterFrame.repaint()
        }
    }

    private val visibilityButton = object : JButton() { //Toggles visibility on selected Token
        private val defaultText = "Visibilité"

        init {
            text = defaultText
            preferredSize = Dimension(150, 40)
            addActionListener {
                selectedElement?.let {
                    ViewManager.toggleVisibility(it)
                    this.text = if (it.isVisible) "Masquer" else "Afficher"
                }
            }
        }

        fun initialize(turnOff: Boolean) {
            if (turnOff) {
                text = defaultText
                isEnabled = false
            } else {
                text = if (selectedElement?.isVisible == true) "Masquer" else "Afficher"
                isEnabled = true
            }
        }
    }

    private val deleteButton = JButton("Supprimer").apply { //Deletes selected Token
        preferredSize = Dimension(150, 40)
        addActionListener {
            selectedElement?.let {
                ViewManager.removeToken(it)
                ViewManager.repaint()
            }
        }
    }

    private val sizeCombo = object : JComboBox<String>(arrayOf("XS", "S", "M", "L", "XL", "XXL")) {
        init {
            addActionListener {
                selectedElement?.let {
                    if (selectedItem != it.size) {
                        when (this.selectedItem) {
                            "XS" -> it.size = Size.XS
                            "S" -> it.size = Size.S
                            "M" -> it.size = Size.M
                            "L" -> it.size = Size.L
                            "XL" -> it.size = Size.XL
                            "XXL" -> it.size = Size.XXL
                        }
                    }
                }
                ViewManager.repaint()
            }
            border = EmptyBorder(0, 0, 0, 0)
        }

        override fun setSelectedItem(element: Any?) {
            if (element == null) {
                this.isEnabled = false
                super.setSelectedItem("S")
            } else {
                this.isEnabled = true
                super.setSelectedItem(element)
            }
        }
    }

    private fun checkTextValue(str: String): Int { // veryfies that the given string is a valid number
        return try {
            Integer.parseInt(str)
        } catch (e: NumberFormatException) {
            (0)
        }
    }

    init {
        this.layout = GridLayout(1, 3)
        this.preferredSize = Dimension(500, 10)
        nameLabel.horizontalTextPosition = JLabel.CENTER

        JPanel().applyAndAppendTo(this) {
            background = BACKGROUND_COLOR_SELECT_PANEL
            border = BorderFactory.createEmptyBorder(0, 0, 0, 10)
            layout = GridLayout(2, 3)

            isOpaque = false
            add(JPanel().apply { isOpaque = false; })
            add(JPanel().apply { isOpaque = false; add(nameLabel) })
            add(JPanel().apply { add(visibilityButton); isOpaque = false })
            add(JPanel().apply { isOpaque = false; })
            add(JPanel().apply { add(sizeCombo); isOpaque = false })
            add(JPanel().apply { add(deleteButton); isOpaque = false; })
        }

        slidePanel = JPanel().apply {
            isOpaque = false
            layout = GridLayout(2, 1)

            add(lifeSlide)
            add(manaSlide)
        }

        JPanel().applyAndAppendTo(this) {
            background = BACKGROUND_COLOR_SELECT_PANEL
            border = BorderFactory.createMatteBorder(0, DEFAULT_BORDER_SIZE, 0, DEFAULT_BORDER_SIZE, Color.BLACK)
            layout = GridLayout(1, 3)

            this.add(slidePanel)
        }


        JPanel().applyAndAppendTo(this) {
            background = BACKGROUND_COLOR_SELECT_PANEL
            layout = GridLayout(1, 1)
        }

        this.background = BACKGROUND_COLOR_SELECT_PANEL
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)

        with(selectedElement) {
            g.color = if (this == null || this.isVisible) {
                Color.BLACK
            } else {
                Color.BLUE
            }
            g.fillRect(15, 15, 110, 110)

            if (this != null) {
                deleteButton.isEnabled = true
                visibilityButton.initialize(false)
                nameLabel.text = this.name

                g.drawImage(this.sprite.image, 20, 20, 100, 100, null)

                lifeSlide = SlideStats.lifeSlide(this)
                manaSlide = SlideStats.manaSlide(this)

                slidePanel.also {
                    it.removeAll()
                    it.add(lifeSlide)
                    it.add(manaSlide)
                }
            } else {
                nameLabel.text = null
                deleteButton.isEnabled = false
                visibilityButton.initialize(true)

                g.color = Color.WHITE
                g.fillRect(20, 20, 100, 100)

                lifeSlide.element = null
                manaSlide.element = null

            }
        }

        this.revalidate()
    }
}
