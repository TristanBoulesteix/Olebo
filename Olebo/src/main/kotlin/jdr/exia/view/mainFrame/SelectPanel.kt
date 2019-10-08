package jdr.exia.view.mainFrame

import java.awt.Graphics
import javax.swing.JPanel

// contains all this info regarding the item selected by the Game Master
//this is a singleton
object SelectPanel : JPanel() {
    fun refresh() { // refreshes the panel's content
        this.repaint()
    }

    public override fun paintComponent(graphics: Graphics) {
        super.paintComponent(graphics)

        // graphics.drawImage(,X,Y,null);
    }
}
