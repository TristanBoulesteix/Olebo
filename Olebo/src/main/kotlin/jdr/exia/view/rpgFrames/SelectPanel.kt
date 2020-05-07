package jdr.exia.view.rpgFrames

import jdr.exia.model.dao.DAO
import jdr.exia.model.element.Element
import jdr.exia.model.element.Size
import jdr.exia.model.utils.isCharacter
import jdr.exia.view.utils.BACKGROUND_COLOR_SELECT_PANEL
import jdr.exia.view.utils.DEFAULT_BORDER_SIZE
import jdr.exia.view.utils.RIGHT_BORDER_BLACK
import jdr.exia.view.utils.applyAndAppend
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
            }
        }

    private val nameLabel = JLabel("Nom").apply {
        horizontalTextPosition = JLabel.CENTER
        border = EmptyBorder(20, 0, 0, 0)
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

    private val manaAmount = JLabel("X/Y").apply {
        // Current amount / max amount
        border = EmptyBorder(0, 20, 10, 0)
    }

    private val manaField = JTextField().apply {
        // Text field to indicate Mana amount
        preferredSize = Dimension(50, 25)
    }

    private val manaButton = JButton("Ajouter Mana").apply {
        //Adds the indicated amount of MP to the character's total
        preferredSize = Dimension(110, 40)
        this.addActionListener {
            if (selectedElement.isCharacter()) {
                transaction(DAO.database) { selectedElement!!.currentMana += checkTextValue(manaField.text) }
            }
            manaField.text = ""
            MasterFrame.repaint()
        }
    }

    private val visibilityButton = JButton("Visible ON/OFF").apply { //Toggles visibility on selected Token
        preferredSize = Dimension(150, 40)
        addActionListener {
            ViewManager.toggleVisibility(selectedElement)
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

    private val sizeCombo = JComboBox(arrayOf("XS", "S", "M", "L", "XL", "XXL")).apply {
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

        JPanel().applyAndAppend(this) {
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

        JPanel().applyAndAppend(this) {
            background = BACKGROUND_COLOR_SELECT_PANEL
            border = BorderFactory.createMatteBorder(0, DEFAULT_BORDER_SIZE, 0, DEFAULT_BORDER_SIZE, Color.BLACK)
            layout = GridLayout(1, 3)
            add(JPanel().apply {
                isOpaque = false
                layout = GridLayout(2, 1)
                add(hpAmount)
                add(manaAmount)
                border = RIGHT_BORDER_BLACK
            })
            add(JPanel().apply {
                isOpaque = false
                isOpaque = false
                layout = GridLayout(2, 1)
                add(JPanel().apply { add(hpField); isOpaque = false })
                add(JPanel().apply { add(manaField); isOpaque = false })
                border = RIGHT_BORDER_BLACK
            })
            add(JPanel().apply {
                isOpaque = false
                isOpaque = false
                layout = GridLayout(2, 1)
                add(JPanel().apply { add(hpButton); isOpaque = false })
                add(JPanel().apply { add(manaButton); isOpaque = false })
            })
        }


        JPanel().applyAndAppend(this) {
            background = BACKGROUND_COLOR_SELECT_PANEL
            border = BorderFactory.createMatteBorder(0, 0, 0, 2, Color.BLACK)
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
                nameLabel.text = this.name

                g.drawImage(this.sprite.image, 20, 20, 100, 100, null)

                if (this.isCharacter()) { //draws informations relative to the char
                    hpAmount.text =
                            "HP: ${this.currentHealth}/${this.maxHP}"
                    hpAmount.isEnabled = true
                    manaAmount.text =
                            "Mana: ${this.currentMana}/${this.maxMana}"
                    manaAmount.isEnabled = true
                } else {
                    hpAmount.apply {
                        this.text = "HP: NA"
                        this.isEnabled = false
                    }

                    manaAmount.apply {
                        this.text = "Mana: NA"
                        this.isEnabled = false
                    }
                }
            } else {
                g.color = Color.WHITE
                g.fillRect(20, 20, 100, 100)
            }
        }

        this.revalidate()
    }
}
