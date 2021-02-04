package jdr.exia.view.frames.home

import jdr.exia.localization.STR_ADD_ACT
import jdr.exia.localization.STR_ELEMENTS
import jdr.exia.localization.Strings
import jdr.exia.view.frames.Reloadable
import jdr.exia.view.frames.home.menu.ActSelectorPanel
import jdr.exia.view.utils.BORDER_BUTTONS
import jdr.exia.view.utils.applyAndAppendTo
import jdr.exia.view.utils.gridBagConstraintsOf
import jdr.exia.viewModel.HomeManager
import java.awt.BorderLayout
import java.awt.Color
import java.awt.GridBagLayout
import javax.swing.BorderFactory
import javax.swing.JButton
import javax.swing.JPanel

class HomePanel : JPanel(), Reloadable {
    private val selectorPanel = ActSelectorPanel()

    init {
        this.layout = BorderLayout()

        JPanel().applyAndAppendTo(this, BorderLayout.NORTH) {
            this.border = BorderFactory.createEmptyBorder(15, 0, 15, 0)
            this.layout = GridBagLayout()

            val elementButton = JButton(Strings[STR_ELEMENTS]).apply {
                this.border = BORDER_BUTTONS
                this.addActionListener {
                    HomeManager.openObjectEditorFrame()
                }
            }

            this.add(elementButton, gridBagConstraintsOf(gridx = 0, gridy = 0, weightx = .5))

            val actButton = JButton(Strings[STR_ADD_ACT]).apply {
                this.border = BORDER_BUTTONS
                this.addActionListener {
                    HomeManager.openActCreatorFrame()
                }
            }

            this.add(actButton, gridBagConstraintsOf(gridx = 1, gridy = 0, weightx = .5))

            this.background = Color.ORANGE
        }

        this.add(selectorPanel, BorderLayout.CENTER)
    }

    override fun reload() {
        selectorPanel.refresh()
    }
}