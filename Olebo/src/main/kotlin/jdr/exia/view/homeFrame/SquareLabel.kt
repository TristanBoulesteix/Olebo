package jdr.exia.view.homeFrame

import java.awt.Color
import java.awt.Dimension
import javax.swing.BorderFactory
import javax.swing.ImageIcon
import javax.swing.JLabel

class SquareLabel(icon: ImageIcon) : JLabel(icon, CENTER) {
    companion object {
        val DIMENSION = Dimension(65, 65)
    }

    init {
        this.preferredSize = DIMENSION
        this.maximumSize = DIMENSION
        this.border = BorderFactory.createLineBorder(Color.YELLOW)
    }
}