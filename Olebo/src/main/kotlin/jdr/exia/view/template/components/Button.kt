package jdr.exia.view.template.components

import java.awt.Color
import javax.swing.BorderFactory
import javax.swing.JButton

class Button(text: String) : JButton(text) {
    init {
        border = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.CYAN, 5),
            BorderFactory.createLineBorder(Color.BLACK, 20)
        )
    }
}