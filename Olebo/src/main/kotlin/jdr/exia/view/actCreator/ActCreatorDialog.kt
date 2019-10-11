package jdr.exia.view.actCreator

import jdr.exia.controller.ActCreatorDialogManager
import jdr.exia.controller.HomeFrameManager
import jdr.exia.pattern.observer.Action
import jdr.exia.pattern.observer.Observable
import jdr.exia.view.template.components.JDialogTemplate
import jdr.exia.view.template.components.PlaceholderTextField
import java.awt.BorderLayout.CENTER
import java.awt.BorderLayout.NORTH
import java.awt.Color
import java.awt.GridBagConstraints
import java.awt.GridBagConstraints.BOTH
import java.awt.GridBagLayout
import java.awt.Window
import javax.swing.BorderFactory
import javax.swing.JPanel

class ActCreatorDialog : JDialogTemplate("Nouvel acte", true) {
    override val observable: Observable = HomeFrameManager

    private val selectorPanel = SceneSelectorPanel()

    init {
        ActCreatorDialogManager.observer = this

        this.defaultCloseOperation = DISPOSE_ON_CLOSE
        this.add(JPanel().apply {
            this.border = BorderFactory.createEmptyBorder(15, 10, 15, 10)
            this.layout = GridBagLayout()

            this.add(PlaceholderTextField("Nom"), GridBagConstraints().apply {
                this.weightx = 1.0
                this.fill = BOTH
            })

            this.background = Color.ORANGE
        }, NORTH)

        this.add(selectorPanel, CENTER)
    }

    override fun update(data: Action): Window? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}