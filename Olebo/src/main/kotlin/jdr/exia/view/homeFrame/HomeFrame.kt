package jdr.exia.view.homeFrame

import jdr.exia.view.actCreator.ActCreatorFrame
import jdr.exia.view.template.components.JFrameTemplate
import java.awt.BorderLayout.CENTER
import java.awt.BorderLayout.NORTH
import java.awt.Color
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.BorderFactory
import javax.swing.JButton
import javax.swing.JPanel

class HomeFrame : JFrameTemplate("Menu principal") {
    init {
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

            val actButton = JButton("Ajouter un acte").apply {
                this.border = BORDER_BUTTONS
                this.addActionListener {
                    ActCreatorFrame().isVisible = true
                }
            }
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