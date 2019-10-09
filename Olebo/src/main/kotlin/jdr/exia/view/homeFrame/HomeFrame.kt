package jdr.exia.view.homeFrame

import java.awt.*
import java.awt.BorderLayout.CENTER
import java.awt.BorderLayout.NORTH
import javax.swing.BorderFactory
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JPanel

class HomeFrame : JFrame("Menu principal") {
    companion object {
        private val DIMENSION_FRAME = Dimension(600, 800)
        private val BORDER_BUTTONS = BorderFactory.createEmptyBorder(15, 15, 15, 15)
    }

    init {
        this.minimumSize = DIMENSION_FRAME
        this.preferredSize = DIMENSION_FRAME
        this.defaultCloseOperation = EXIT_ON_CLOSE
        this.setLocationRelativeTo(null)

        this.layout = BorderLayout()

        this.add(JPanel().apply {
            this.border = BorderFactory.createEmptyBorder(15, 0, 15, 0)
            this.layout = GridBagLayout()

            val elementButton = JButton("Éléments")
            elementButton.border = BORDER_BUTTONS
            val cElementButton = GridBagConstraints().apply {
                this.gridx = 0
                this.gridy = 0
                this.weightx = .5
            }
            this.add(elementButton, cElementButton)

            val actButton = JButton("Ajouter un acte")
            actButton.border = BORDER_BUTTONS
            val cActButton = GridBagConstraints().apply {
                this.gridx = 1
                this.gridy = 0
                this.weightx = .5
            }
            this.add(actButton, cActButton)

            this.background = Color.ORANGE
        }, NORTH)

        this.add(ActSelectorPanel(), CENTER)

        this.pack()
    }
}