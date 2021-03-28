package jdr.exia.view.frames.home.panels

import jdr.exia.localization.STR_ADD_ACT
import jdr.exia.localization.STR_DOUBLE_CLICK_OPEN_ACT
import jdr.exia.localization.STR_ELEMENTS
import jdr.exia.localization.StringLocale
import jdr.exia.model.dao.DAO
import jdr.exia.model.dao.getIcon
import jdr.exia.view.utils.BORDER_BUTTONS
import jdr.exia.view.utils.applyAndAddTo
import jdr.exia.view.utils.components.templates.ItemPanel
import jdr.exia.view.utils.components.templates.SelectorPanel
import jdr.exia.view.utils.event.addDoubleClickListener
import jdr.exia.view.utils.gridBagConstraintsOf
import jdr.exia.viewModel.HomeManager
import java.awt.BorderLayout
import java.awt.Color
import java.awt.GridBagLayout
import javax.swing.BorderFactory
import javax.swing.JButton
import javax.swing.JPanel

class ActsPanel(private val homeManager: HomeManager) : HomePanel() {
    private val selectorPanel = ActSelectorPanel()

    init {
        this.layout = BorderLayout()

        JPanel().applyAndAddTo(this, BorderLayout.NORTH) {
            this.border = BorderFactory.createEmptyBorder(15, 0, 15, 0)
            this.layout = GridBagLayout()

            val elementButton = JButton(StringLocale[STR_ELEMENTS]).apply {
                this.border = BORDER_BUTTONS
                this.addActionListener {
                    homeManager.openObjectEditorFrame()
                }
            }

            this.add(elementButton, gridBagConstraintsOf(gridx = 0, gridy = 0, weightx = .5))

            val actButton = JButton(StringLocale[STR_ADD_ACT]).apply {
                this.border = BORDER_BUTTONS
                this.addActionListener {
                    homeManager.openActCreatorFrame()
                }
            }

            this.add(actButton, gridBagConstraintsOf(gridx = 1, gridy = 0, weightx = .5))

            this.background = Color.ORANGE
        }

        this.add(selectorPanel, BorderLayout.CENTER)
    }

    override fun reload() = selectorPanel.reload()

    /**
     * This panel's goals is to display all acts in the database
     */
    private inner class ActSelectorPanel : SelectorPanel(PairArrayBuilder { DAO.getActsList() }) {
        override fun builder(id: Int, name: String): ItemPanel {
            return ActPanel(id, name)
        }

        /**
         * This panel display an Act
         */
        private inner class ActPanel(id: Int, name: String) : ItemPanel(id, name) {
            init {
                listOf(nameLabel, namePanel).forEach {
                    it.addDoubleClickListener { homeManager.launchAct(id) }
                    it.toolTipText = StringLocale[STR_DOUBLE_CLICK_OPEN_ACT]
                }
                this.nameLabel.isEnabled = false

                this.add(SquareLabel(getIcon("edit_icon", homeManager.javaClass), homeManager::updateAct))

                this.add(SquareLabel(getIcon("delete_icon", homeManager.javaClass), homeManager::deleteAct))
            }
        }
    }
}