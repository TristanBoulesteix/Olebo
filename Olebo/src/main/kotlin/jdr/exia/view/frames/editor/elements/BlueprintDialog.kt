package jdr.exia.view.frames.editor.elements

import jdr.exia.localization.*
import jdr.exia.view.utils.BACKGROUND_COLOR_ORANGE
import jdr.exia.view.utils.applyAndAppendTo
import jdr.exia.view.utils.components.templates.JDialogTemplate
import jdr.exia.view.utils.event.addClickListener
import jdr.exia.view.utils.gridBagConstraintsOf
import jdr.exia.viewModel.BlueprintManager
import jdr.exia.viewModel.observer.Action
import java.awt.BorderLayout
import java.awt.Font
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.BorderFactory
import javax.swing.JComboBox
import javax.swing.JPanel
import jdr.exia.model.element.Type as TypeBlueprint

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

        this.addClickListener { requestFocusInWindow() }

        JPanel().applyAndAppendTo(this, BorderLayout.NORTH) {
            this.border = BorderFactory.createEmptyBorder(15, 10, 15, 10)
            this.layout = GridBagLayout()

            this.add(comboType, gridBagConstraintsOf(fill = GridBagConstraints.BOTH, weightx = 1.0))

            this.background = BACKGROUND_COLOR_ORANGE
        }

        this.add(panel, BorderLayout.CENTER)
    }

    override fun update(data: Action) {
        when (data) {
            Action.Refresh -> this.panel.reload()
            Action.Dispose -> this.dispose()
        }
    }
}