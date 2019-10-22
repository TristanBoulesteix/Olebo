package jdr.exia.view.editor.acts

import jdr.exia.controller.ActCreatorManager
import jdr.exia.model.act.Act
import jdr.exia.pattern.observer.Action
import jdr.exia.view.utils.BACKGROUND_COLOR_LIGHT_BLUE
import jdr.exia.view.utils.BACKGROUND_COLOR_ORANGE
import jdr.exia.view.utils.BORDER_BUTTONS
import jdr.exia.view.utils.components.JDialogTemplate
import jdr.exia.view.utils.components.PlaceholderTextField
import jdr.exia.view.utils.showPopup
import java.awt.BorderLayout
import java.awt.BorderLayout.*
import java.awt.GridBagConstraints
import java.awt.GridBagConstraints.BOTH
import java.awt.GridBagLayout
import javax.swing.BorderFactory
import javax.swing.JButton
import javax.swing.JPanel

/**
 * This JDialog allows us to create or update an act.
 */
class ActEditorDialog : JDialogTemplate("Nouvel acte") {
    private val manager = ActCreatorManager()
    override val observable = manager

    private val selectorPanel = SceneSelectorPanel(manager)
    private val nameField = PlaceholderTextField("Nom")

    init {
        this.manager.observer = this

        this.defaultCloseOperation = DISPOSE_ON_CLOSE

        this.add(JPanel().apply {
            this.border = BorderFactory.createEmptyBorder(15, 10, 15, 10)
            this.layout = GridBagLayout()

            this.add(nameField, GridBagConstraints().apply {
                this.weightx = 1.0
                this.fill = BOTH
            })

            this.background = BACKGROUND_COLOR_ORANGE
        }, NORTH)

        this.add(selectorPanel, CENTER)

        val panel = JPanel().apply {
            this.border = BorderFactory.createEmptyBorder(10, 20, 10, 20)
            this.layout = BorderLayout()
            this.background = BACKGROUND_COLOR_LIGHT_BLUE
            this.add(JButton("Valider").apply {
                this.addActionListener {
                    if (nameField.text.isNotEmpty() && this@ActEditorDialog.manager.tempScenes.isNotEmpty() && this@ActEditorDialog.manager.saveAct(nameField.text)) {
                        this@ActEditorDialog.dispose()
                    } else {
                        showPopup("Désolé, un acte avec le même nom existe déjà ou les données de l'acte sont invalides !", this@ActEditorDialog)
                    }
                }
                this.border = BORDER_BUTTONS
            }, CENTER)
        }

        this.add(panel, SOUTH)
    }

    /**
     * Fill the dialog frame with act data. This changes its state to "update".
     *
     * @param act The act to update
     */
    fun fillWithAct(act: Act) : ActEditorDialog {
        this.nameField.text = act.name
        this.manager.updateAct(act.scenes, act.id.value)
        return this
    }

    override fun update(data: Action) {
        when (data) {
            Action.DISPOSE -> this.dispose()
            Action.REFRESH -> this.selectorPanel.refresh()
        }
    }
}