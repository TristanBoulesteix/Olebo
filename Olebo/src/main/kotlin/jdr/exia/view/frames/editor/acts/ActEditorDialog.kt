package jdr.exia.view.frames.editor.acts

import jdr.exia.localization.*
import jdr.exia.model.act.Act
import jdr.exia.view.utils.*
import jdr.exia.view.utils.components.templates.JDialogTemplate
import jdr.exia.view.utils.components.templates.PlaceholderTextField
import jdr.exia.viewModel.ActCreatorManager
import jdr.exia.viewModel.pattern.observer.Action
import java.awt.BorderLayout
import java.awt.BorderLayout.*
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.BorderFactory
import javax.swing.JButton
import javax.swing.JPanel

/**
 * This JDialog allows us to create or update an act.
 */
class ActEditorDialog : JDialogTemplate(Strings[STR_NEW_ACT]) {
    private val manager = ActCreatorManager()
    override val observable = manager

    private val selectorPanel = SceneSelectorPanel(manager)
    private val nameField = PlaceholderTextField(Strings[STR_NAME])

    init {
        this.manager.observer = this

        this.defaultCloseOperation = DISPOSE_ON_CLOSE

        this.add(JPanel().apply {
            this.border = BorderFactory.createEmptyBorder(15, 10, 15, 10)
            this.layout = GridBagLayout()

            this.add(nameField, gridBagConstraintsOf(weightx = 1.0, fill = GridBagConstraints.BOTH))

            this.background = BACKGROUND_COLOR_ORANGE
        }, NORTH)

        this.add(selectorPanel, CENTER)

        val panel = JPanel().apply {
            this.border = BorderFactory.createEmptyBorder(10, 20, 10, 20)
            this.layout = BorderLayout()
            this.background = BACKGROUND_COLOR_LIGHT_BLUE
            this.add(JButton(Strings[STR_CONFIRM]).apply {
                this.addActionListener {
                    if (nameField.text.isNotEmpty() && this@ActEditorDialog.manager.tempScenes.isNotEmpty() && this@ActEditorDialog.manager.saveAct(
                            nameField.text
                        )
                    ) {
                        this@ActEditorDialog.dispose()
                    } else {
                        showPopup(Strings[ST_ACT_ALREADY_EXISTS], this@ActEditorDialog)
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
    fun fillWithAct(act: Act): ActEditorDialog {
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