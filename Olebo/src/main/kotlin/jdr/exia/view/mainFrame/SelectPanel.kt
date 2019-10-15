package jdr.exia.view.mainFrame

import com.sun.jdi.connect.Connector
import jdr.exia.model.element.Character
import jdr.exia.model.element.Element
import java.awt.Color
import java.awt.Graphics
import javax.swing.JPanel


// contains all this info regarding the item selected by the Game Master
//this is a singleton
object SelectPanel : JPanel() {
    var selectedElement: Element? = null

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
