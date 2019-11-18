package jdr.exia.view.mainFrame

import jdr.exia.controller.ViewManager
import jdr.exia.model.dao.DAO
import jdr.exia.model.element.Element
import jdr.exia.model.element.Size
import jdr.exia.model.utils.isCharacter
import org.jetbrains.exposed.sql.transactions.transaction
import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics
import java.awt.GridLayout
import javax.swing.*
import javax.swing.border.EmptyBorder


// contains all this info regarding the item selected by the Game Master
//this is a singleton
object SelectPanel : JPanel() {
    var selectedElement: Element? = null
        set(value){
            field = value
            if (selectedElement!=null) {
               sizeCombo.selectedItem =  selectedElement!!.size.name
            }
        }

    private val nameLabel = JLabel("Name").apply { horizontalTextPosition = JLabel.CENTER; border = EmptyBorder(20, 0, 0, 0) }

    private val hpAmount = JLabel("X/Y").apply { border = EmptyBorder(0, 20, 10, 0) }
    private val hpField = JTextField().apply { preferredSize = Dimension(50, 25); border = EmptyBorder(20, 0, 0, 0) }
    private val hpButton = JButton("ADD HP").apply {
        preferredSize = Dimension(100, 40)
        this.addActionListener {
            if (selectedElement.isCharacter()) {
                selectedElement!!.currentHealth += checkTextValue(hpField.text)
            }
            hpField.text = ""
            MasterFrame.repaint()
        }
    }

    private val manaAmount = JLabel("X/Y").apply { border = EmptyBorder(0, 20, 10, 0) }
    private val manaField = JTextField().apply { preferredSize = Dimension(50, 25);border = EmptyBorder(10, 0, 0, 0) }
    private val manaButton = JButton("ADD MANA").apply {
        preferredSize = Dimension(110, 40)
        this.addActionListener {
            if (selectedElement.isCharacter()) {
                selectedElement!!.currentMana += checkTextValue(manaField.text)
            }
            manaField.text = ""
            MasterFrame.repaint()
        }
    }

    private val visibilityButton = JButton("Toggle  Visible").apply {
        preferredSize = Dimension(150, 40)
        addActionListener {
            ViewManager.toggleVisibility(selectedElement)
        }
    }

    private val deleteButton = JButton("Delete").apply {
        preferredSize = Dimension(150, 40)
        addActionListener {
            if (selectedElement != null) {

                ViewManager.removeToken(selectedElement!!)
                ViewManager.repaint()
                }
        }
    }

    private val sizeCombo = JComboBox(arrayOf("XS", "S", "M", "L", "XL", "XXL")).apply {
        addActionListener {
            if (selectedElement != null && selectedItem != selectedElement!!.size) {
                when (this.selectedItem) {
                    "XS" -> selectedElement!!.size = Size.XS
                    "S" -> selectedElement!!.size = Size.S
                    "M" -> selectedElement!!.size = Size.M
                    "L" -> selectedElement!!.size = Size.L
                    "XL" -> selectedElement!!.size = Size.XL
                    "XXL" -> selectedElement!!.size = Size.XXL
                }
            }

            ViewManager.repaint()
        }
        border = EmptyBorder(0, 0, 0, 0)
    }

    private fun checkTextValue(str: String): Int {
        try {
            return Integer.parseInt(str)
        } catch (e: NumberFormatException) {

            return (0)
        }

    }


    init {
        this.layout = GridLayout(1, 3)
        this.preferredSize = Dimension(500, 10)
        nameLabel.horizontalTextPosition = JLabel.CENTER

        val leftPanel = JPanel().apply {
            background = Color.gray
            border = BorderFactory.createLineBorder(Color.black)
            layout = GridLayout(2, 3).apply {}

            isOpaque = false
            add(JPanel().apply { isOpaque = false;})
            add(JPanel().apply { isOpaque = false; add(nameLabel) })
            add(JPanel().apply { add(visibilityButton); isOpaque = false })
            add(JPanel().apply { isOpaque = false;})
            add(JPanel().apply { add(sizeCombo); isOpaque = false })
            add(JPanel().apply { add(deleteButton); isOpaque = false; })
        }

        val centerPanel = JPanel().apply {
            background = Color.gray
            border = BorderFactory.createLineBorder(Color.black)
            layout = GridLayout(1, 3).apply {}
            add(
                JPanel().apply {
                    isOpaque = false
                    layout = GridLayout(2, 1)
                    add(hpAmount)
                    add(manaAmount)
                    border = BorderFactory.createLineBorder(Color.black)
                })
            add(JPanel().apply {
                isOpaque = false;
                isOpaque = false
                layout = GridLayout(2, 1)
                add(JPanel().apply { add(hpField); isOpaque = false })
                add(JPanel().apply { add(manaField); isOpaque = false })
                border = BorderFactory.createLineBorder(Color.black)
            })
            add(JPanel().apply {
                isOpaque = false;
                isOpaque = false
                layout = GridLayout(2, 1)
                add(JPanel().apply { add(hpButton); isOpaque = false })
                add(JPanel().apply { add(manaButton); isOpaque = false })
                border = BorderFactory.createLineBorder(Color.black)
            })
        }


        val rightPanel = JPanel().apply {
            background = Color.gray
            border = BorderFactory.createLineBorder(Color.black)
            layout = GridLayout(1, 1)
        }


        add(leftPanel)
        add(centerPanel)
        add(rightPanel)


        this.background = Color.GRAY
    }


    public override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        if (selectedElement != null) {

            if (selectedElement!!.isVisible) {
                g.color = Color.BLACK
            } else {
                g.color = Color.BLUE
            }
            g.fillRect(15, 15, 110, 110)
            nameLabel.text = selectedElement!!.name

            g.drawImage(selectedElement!!.sprite.image, 20, 20, 100, 100, null)

            if (selectedElement.isCharacter()) {
                hpAmount.text =
                    "HP: ${selectedElement!!.currentHealth}/${selectedElement!!.maxHP}"
                manaAmount.text =
                    "Mana: ${selectedElement!!.currentMana}/${selectedElement!!.maxMana}"
            } else {
                hpAmount.text = "HP: NA";manaAmount.text = "Mana: NA"
            }
        }
    }






}
