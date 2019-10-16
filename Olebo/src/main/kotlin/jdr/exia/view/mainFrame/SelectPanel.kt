package jdr.exia.view.mainFrame

import com.sun.jdi.connect.Connector
import jdr.exia.controller.HomeManager
import jdr.exia.controller.ViewManager
import jdr.exia.model.element.Character
import jdr.exia.model.element.Element
import jdr.exia.model.element.Size
import java.awt.*
import javax.swing.*


// contains all this info regarding the item selected by the Game Master
//this is a singleton
object SelectPanel : JPanel() {
    var selectedElement: Element? = null



    private val nameLabel = JLabel("Name").apply { horizontalTextPosition = JLabel.CENTER }

    private val hpAmount = JLabel("X/Y")
    private val hpField = JTextField().apply { preferredSize = Dimension(50,25) }
    private val hpButton = JButton("ADD HP").apply {
        preferredSize = Dimension(100,40)
        this.addActionListener {
            if(selectedElement is Character) {
                (selectedElement as Character).currentHealth += checkTextValue(hpField.text)
            }
            hpField.text = ""
            MasterFrame.repaint()
        }
    }

    private val manaAmount = JLabel("X/Y")
    private val manaField = JTextField().apply { preferredSize = Dimension(50,25) }
    private val manaButton = JButton("ADD MANA").apply {
        preferredSize = Dimension(110,40)
        this.addActionListener {
            if(selectedElement is Character) {
                (selectedElement as Character).currentMana += checkTextValue(manaField.text)
            }
            manaField.text = ""
            MasterFrame.repaint()
        }
    }

    private val visibilityButton = JButton("Toggle \n Visible").apply {
        preferredSize = Dimension(150,40)
        addActionListener{
            ViewManager.toggleVisibility(selectedElement)
        }
    }

    //private val sizeCombo = JComboBox<Size>() //TODO: Complete the Size drop down menu

    private fun checkTextValue(str: String): Int{
        try {
            return Integer.parseInt(str)
        } catch (e: NumberFormatException) {
            println("Wrong number")
            return(0)
        }

    }


    init{
        this.layout = GridLayout(1,4)
        this.preferredSize = Dimension(500,10)
        nameLabel.horizontalTextPosition = JLabel.CENTER

        val leftPanel = JPanel().apply { background = Color.gray
            border = BorderFactory.createLineBorder(Color.black)
            layout = GridLayout(2,1).apply {}

            isOpaque = false
            add(JPanel().apply { isOpaque = false; add(nameLabel)})
            add(JPanel().apply { add(visibilityButton); isOpaque = false})

        }

        val centerPanel = JPanel().apply { background = Color.gray
            border = BorderFactory.createLineBorder(Color.black)
            layout = GridLayout(1,3).apply {}
            add(
                JPanel().apply {
                    isOpaque = false
                    layout = GridLayout(2,1)
                    add(hpAmount)
                    add(manaAmount)
                    border = BorderFactory.createLineBorder(Color.black)
                    })
            add(JPanel().apply { isOpaque = false;
                isOpaque = false
                layout = GridLayout(2,1)
                add(JPanel().apply { add(hpField); isOpaque = false})
                add(JPanel().apply { add(manaField); isOpaque = false})
                border = BorderFactory.createLineBorder(Color.black)
            })
            add(JPanel().apply { isOpaque = false;
                isOpaque = false
                layout = GridLayout(2,1)
                add(JPanel().apply { add(hpButton); isOpaque = false})
                add(JPanel().apply { add(manaButton); isOpaque = false})
                border = BorderFactory.createLineBorder(Color.black)
            })
        }


        val rightPanel = JPanel().apply { background = Color.gray
            border = BorderFactory.createLineBorder(Color.black)
            layout = GridLayout(2,2)
        }


        add(leftPanel)
        add(centerPanel)
        add(rightPanel)


        this.background = Color.GRAY
    }



    public override fun paintComponent(g: Graphics) {

        super.paintComponent(g)


        if(selectedElement!=null) {
            if(selectedElement!!.visible){
                g.color = Color.BLACK} else { g.color = Color.BLUE}
            g.fillRect(45,10,110,110)
            nameLabel.text = selectedElement!!.name

            g.drawImage(selectedElement!!.sprite.image,50,15,100,100,null)

            if(selectedElement is Character)
            {
                hpAmount.text = "HP: ${(selectedElement as Character).currentHealth}/${(selectedElement as Character).maxHealth}"
                manaAmount.text = "Mana: ${(selectedElement as Character).currentMana}/${(selectedElement as Character).maxMana}"
            } else{hpAmount.text = "HP: NA";manaAmount.text = "Mana: NA"}
    }
}
}
