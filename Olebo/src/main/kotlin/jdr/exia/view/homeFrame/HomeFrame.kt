package jdr.exia.view.homeFrame

import jdr.exia.controller.HomeFrameManager
import jdr.exia.pattern.Action
import jdr.exia.pattern.Observable
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
    override val observable: Observable = HomeFrameManager

    private val selectorPanel =  ActSelectorPanel()

    init {
        HomeFrameManager.observer = this

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
                    HomeFrameManager.openActCreatorFrame()
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

        this.add(selectorPanel, CENTER)

        this.pack()
    }

    override fun update(data: Action) {
        when(data) {
            Action.DISPOSE -> this.dispose()
            Action.REFRESH -> this.selectorPanel.refresh()
        }
    }
}