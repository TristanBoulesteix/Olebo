package jdr.exia.view.editor

import jdr.exia.controller.ActCreatorManager
import jdr.exia.pattern.observer.Action
import jdr.exia.pattern.observer.Observable
import jdr.exia.view.template.BACKGROUND_COLOR_LIGHT_BLUE
import jdr.exia.view.template.BACKGROUND_COLOR_ORANGE
import jdr.exia.view.template.BORDER_BUTTONS
import jdr.exia.view.template.components.JDialogTemplate
import jdr.exia.view.template.components.PlaceholderTextField
import java.awt.BorderLayout
import java.awt.BorderLayout.*
import java.awt.GridBagConstraints
import java.awt.GridBagConstraints.BOTH
import java.awt.GridBagLayout
import java.awt.Window
import javax.swing.BorderFactory
import javax.swing.JButton
import javax.swing.JPanel

object ActCreatorDialog : JDialogTemplate("Nouvel acte") {
    private val manager = ActCreatorManager()
    override val observable: Observable = manager

    private val selectorPanel = SceneSelectorPanel(manager)

    init {
        this.manager.observer = this

        this.defaultCloseOperation = DISPOSE_ON_CLOSE
        this.add(JPanel().apply {
            this.border = BorderFactory.createEmptyBorder(15, 10, 15, 10)
            this.layout = GridBagLayout()

            this.add(PlaceholderTextField("Nom"), GridBagConstraints().apply {
                this.weightx = 1.0
                this.fill = BOTH
            })

            this.background = BACKGROUND_COLOR_ORANGE
        }, NORTH)

        this.add(selectorPanel, CENTER)

        val panel = JPanel().apply {
            this.border = BorderFactory.createEmptyBorder(10,20,10,20)
            this.layout = BorderLayout()
            this.background = BACKGROUND_COLOR_LIGHT_BLUE
            this.add(JButton("Valider").apply {
                this.border = BORDER_BUTTONS
            }, CENTER)
        }

        this.add(panel, SOUTH)
    }

    override fun update(data: Action): Window? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}