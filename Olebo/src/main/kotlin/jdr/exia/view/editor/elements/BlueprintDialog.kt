package jdr.exia.view.editor.elements

import jdr.exia.controller.BlueprintManager
import jdr.exia.pattern.observer.Action
import jdr.exia.view.utils.BACKGROUND_COLOR_ORANGE
import jdr.exia.view.utils.components.JDialogTemplate
import java.awt.BorderLayout
import java.awt.Font
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.BorderFactory
import javax.swing.JComboBox
import javax.swing.JPanel
import jdr.exia.model.element.Type as TypeBlueprint

class BlueprintDialog : JDialogTemplate("Liste des objets") {
    private val manager = BlueprintManager()
    override val observable = manager

    private val comboType = JComboBox(arrayOf("Objets", "PJ", "PNJ")).apply {
        this.font = Font("Tahoma", Font.BOLD, 20)
        this.addActionListener {
            when (this.selectedItem) {
                "Objets" -> manager.type = TypeBlueprint.OBJECT
                "PJ" -> manager.type = TypeBlueprint.PJ
                "PNJ" -> manager.type = TypeBlueprint.PNJ
            }
        }
    }
    private val panel = ElementSelectorPanel(manager).apply {
        this.isFocusable = true
    }

    init {
        this.manager.observer = this

        this.defaultCloseOperation = DISPOSE_ON_CLOSE
        this.contentPane.isFocusable = true

        this.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent?) {
                super.mouseClicked(e)
                requestFocusInWindow()
            }
        })

        this.add(JPanel().apply {
            this.border = BorderFactory.createEmptyBorder(15, 10, 15, 10)
            this.layout = GridBagLayout()

            this.add(comboType, GridBagConstraints().apply {
                this.weightx = 1.0
                this.fill = GridBagConstraints.BOTH
            })

            this.background = BACKGROUND_COLOR_ORANGE
        }, BorderLayout.NORTH)

        this.add(panel, BorderLayout.CENTER)
    }

    override fun update(data: Action) {
        when (data) {
            Action.REFRESH -> this.panel.refresh()
            Action.DISPOSE -> this.dispose()
        }
    }
}