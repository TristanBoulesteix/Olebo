package jdr.exia.view.editor.elements

import jdr.exia.controller.ElementManagerTemplate
import jdr.exia.pattern.observer.Action
import jdr.exia.view.utils.BACKGROUND_COLOR_ORANGE
import jdr.exia.view.utils.components.JDialogTemplate
import java.awt.BorderLayout
import java.awt.Font
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.BorderFactory
import javax.swing.JComboBox
import javax.swing.JPanel

class ObjectEditorDialog : JDialogTemplate("LIste des objets") {
    private val manager = ElementManagerTemplate()
    override val observable = manager

    private val comboType = JComboBox(arrayOf("Objets", "PJ / PNJ")).apply {
        this.font = Font("Tahoma", Font.BOLD, 20)
    }

    init {
        this.manager.observer = this

        this.defaultCloseOperation = DISPOSE_ON_CLOSE

        this.add(JPanel().apply {
            this.border = BorderFactory.createEmptyBorder(15, 10, 15, 10)
            this.layout = GridBagLayout()

            this.add(comboType, GridBagConstraints().apply {
                this.weightx = 1.0
                this.fill = GridBagConstraints.BOTH
            })

            this.background = BACKGROUND_COLOR_ORANGE
        }, BorderLayout.NORTH)
    }

    override fun update(data: Action) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}