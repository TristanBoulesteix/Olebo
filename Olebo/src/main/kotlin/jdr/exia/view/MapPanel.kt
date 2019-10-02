package jdr.exia.view

import java.awt.Color
import java.awt.Graphics
import javax.swing.JPanel

// This panel contains the map and all the objects placed within it

object MapPanel : JPanel() {
    init {
        this.background = Color.blue
    }

    override fun paintComponent(graphics: Graphics) {
        super.paintComponent(graphics)

        // graphics.drawImage(,X,Y,null);
    }

    fun refresh() { // refreshes the panel's content
        this.repaint()
    }
}
