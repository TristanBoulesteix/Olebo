package jdr.exia.view.homeFrame

import jdr.exia.controller.HomeFrameController
import java.awt.*
import java.awt.BorderLayout.CENTER
import java.awt.BorderLayout.NORTH
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.border.EmptyBorder

class HomeFrame : JFrame("Menu principal") {
    companion object {
        private val DIMENSION = Dimension(600, 800)
    }

    private val controller = HomeFrameController

    init {
        this.minimumSize = DIMENSION
        this.preferredSize = DIMENSION
        this.defaultCloseOperation = EXIT_ON_CLOSE
        this.setLocationRelativeTo(null)

        this.layout = BorderLayout()

        this.add(JPanel().apply {
            this.border = EmptyBorder(15, 0, 15, 0)
            this.layout = GridBagLayout()

            val elementButton = JButton("Éléments")
            val cElementButton = GridBagConstraints().apply {
                this.gridx = 0
                this.gridy = 0
                this.weightx = .5
            }
            this.add(elementButton, cElementButton)

            val actButton = JButton("Ajouter un acte")
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