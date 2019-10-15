package jdr.exia.view.mainFrame

import com.sun.jdi.connect.Connector
import jdr.exia.model.element.Character
import jdr.exia.model.element.Element
import java.awt.Color
import java.awt.Graphics
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.JButton
import javax.swing.JPanel


// contains all this info regarding the item selected by the Game Master
//this is a singleton
object SelectPanel : JPanel() {
    var selectedElement: Element? = null

    init{
        this.layout = GridBagLayout()
        val hpPlus = JButton("+")
        val hpMinus = JButton("-")
        val manaPlus = JButton("+")
        val manaMinus = JButton("-")

        val hpPlusConst = GridBagConstraints().apply {
            this.gridx = 0
            this.gridy = 0
            this.weightx = 0.5
            this.weighty = 0.5
        }

        val hpMinusConst = GridBagConstraints().apply {
            this.gridx = 0
            this.gridy = 1
            this.weightx = 0.5
            this.weighty = 0.5
        }

        val manaPlusConst = GridBagConstraints().apply {
            this.gridx = 1
            this.gridy = 0
            this.weightx = 0.1
            this.weighty = 0.5
        }
        val manaMinusConst = GridBagConstraints().apply {
            this.gridx = 1
            this.gridy = 1
            this.weightx =0.1
            this.weighty = 0.5
        }
        this.background = Color.GRAY
        add(hpPlus,hpPlusConst)
        add(hpMinus,hpMinusConst)
        add(manaPlus,manaPlusConst)
        add(manaMinus,manaMinusConst)




    }

    fun refresh() { // refreshes the panel's content
        this.repaint()
    }

    public override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        if(selectedElement!=null) {
            println("test")
            g.drawImage(selectedElement!!.sprite.image, 20, 20, 64, 64, null)
            if(selectedElement is Character){
                g.drawString("Health:${(selectedElement as Character).currentHealth}/${(selectedElement as Character).maxHealth}", 100, 250)
            }
        }
    }
}
