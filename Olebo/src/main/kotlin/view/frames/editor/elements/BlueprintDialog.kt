package view.frames.editor.elements

import model.internationalisation.*
import view.utils.BACKGROUND_COLOR_ORANGE
import view.utils.components.JDialogTemplate
import viewModel.BlueprintManager
import viewModel.pattern.observer.Action
import java.awt.BorderLayout
import java.awt.Font
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.BorderFactory
import javax.swing.JComboBox
import javax.swing.JPanel
import model.element.Type as TypeBlueprint

class BlueprintDialog : JDialogTemplate(Strings[STR_OBJECT_LIST]) {
    private val manager = BlueprintManager()
    override val observable = manager

    private val comboType = JComboBox(arrayOf(Strings[STR_OBJECTS], Strings[STR_PC], Strings[STR_NPC])).apply {
        this.font = Font("Tahoma", Font.BOLD, 20)
        this.addActionListener {
            when (this.selectedItem) {
                Strings[STR_OBJECTS] -> manager.type = TypeBlueprint.OBJECT
                Strings[STR_PC] -> manager.type = TypeBlueprint.PJ
                Strings[STR_NPC] -> manager.type = TypeBlueprint.PNJ
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