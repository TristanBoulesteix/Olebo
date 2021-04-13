package jdr.exia.view.frames.home.panels

import jdr.exia.localization.*
import jdr.exia.model.dao.getIcon
import jdr.exia.model.element.Blueprint
import jdr.exia.model.element.Type
import jdr.exia.view.utils.*
import jdr.exia.view.utils.components.templates.ItemPanel
import jdr.exia.view.utils.components.templates.SelectorPanel
import jdr.exia.view.utils.event.addClickListener
import jdr.exia.view.utils.event.addFocusGainedListener
import jdr.exia.view.utils.event.addFocusLostListener
import jdr.exia.view.utils.factories.TitlePanel
import jdr.exia.view.utils.factories.buildTitleItemPanel
import jdr.exia.viewModel.BlueprintManager
import jdr.exia.viewModel.HomeManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.awt.BorderLayout
import java.awt.Font
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.BorderFactory
import javax.swing.JButton
import javax.swing.JComboBox
import javax.swing.JPanel

class BlueprintEditorPanel(homeManager: HomeManager, homeAvailable: Boolean = true) : HomePanel() {
    private val manager = BlueprintManager(homeManager)

    private val pairArrayBuilder = SelectorPanel.PairArrayBuilder {
        manager.elements.map { Pair(it.id.value.toString(), it.name) }.toTypedArray()
    }

    private val comboType =
        JComboBox(arrayOf(StringLocale[STR_OBJECTS], StringLocale[STR_PC], StringLocale[STR_NPC])).apply {
            this.font = Font("Tahoma", Font.BOLD, 20)
            this.addActionListener {
                when (this.selectedItem) {
                    StringLocale[STR_OBJECTS] -> manager.type = Type.OBJECT
                    StringLocale[STR_PC] -> manager.type = Type.PJ
                    StringLocale[STR_NPC] -> manager.type = Type.PNJ
                }
            }
        }

    private val selectorPanel: BlueprintSelectorPanel

    init {
        this.layout = BorderLayout()
        this.isFocusable = true

        this.addClickListener { requestFocusInWindow() }

        JPanel().applyAndAddTo(this, BorderLayout.NORTH) {
            this.border = BorderFactory.createEmptyBorder(15, 10, 15, 10)
            this.layout = GridBagLayout()

            this.add(comboType, gridBagConstraintsOf(fill = GridBagConstraints.BOTH, weightx = 1.0))

            this.background = BACKGROUND_COLOR_ORANGE
        }

        selectorPanel = BlueprintSelectorPanel().apply {
            this.isFocusable = true
        }

        this.add(selectorPanel, BorderLayout.CENTER)

        if (homeAvailable)
            JPanel().applyAndAddTo(this, BorderLayout.SOUTH) {
                this.border = BorderFactory.createEmptyBorder(10, 20, 10, 20)
                this.layout = BorderLayout()
                this.background = BACKGROUND_COLOR_LIGHT_BLUE

                JButton(StringLocale[STR_BACK]).applyAndAddTo(this) {
                    this.border = BORDER_BUTTONS

                    this.addActionListener {
                        selectorPanel.submitName()
                        homeManager.goHome()
                    }
                }
            }
    }

    override fun reload() = this.selectorPanel.reload()

    private data class BlueprintToSave(val id: Int, val text: String)

    /**
     * Panel to select elements. Show a list of blueprints
     *
     */
    inner class BlueprintSelectorPanel : SelectorPanel(pairArrayBuilder) {
        override fun builder(id: Int, name: String) =
            (if (manager.type != Type.OBJECT) CharacterPanel(id, name) else ObjectPanel(id, name))

        private var blueprintToSave: BlueprintToSave? = null

        private val titlePanel = JPanel()
        private val cTitleItem
            get() = gridBagConstraintsOf(
                fill = GridBagConstraints.BOTH,
                weightx = 1.0,
                gridx = 0,
                gridy = 0
            )

        private var titleContentPanel: TitlePanel

        init {
            this.isFocusable = true

            titlePanel.applyAndAddTo(this, BorderLayout.NORTH) {
                this.layout = GridBagLayout()

                titleContentPanel = buildTitleItemPanel(manager)
                this.add(titleContentPanel, cTitleItem)
                this.revalidate()
            }
            this.reload()
        }

        override fun reload() {
            titlePanel.remove(titleContentPanel)
            titleContentPanel = buildTitleItemPanel(manager)
            titlePanel.add(titleContentPanel, cTitleItem)
            super.reload()
        }

        fun submitName() = blueprintToSave?.let {
            manager.updateName(it.id, it.text)
        }

        /**
         * Display an object and its options
         */
        private inner class ObjectPanel(id: Int, name: String) : ItemPanel(id, name) {
            init {
                this.nameLabel.apply {
                    this.isEditable = true
                    this.addFocusGainedListener {
                        blueprintToSave = BlueprintToSave(id, this@apply.text)
                    }
                    this.addFocusLostListener {
                        if (!it.isTemporary) {
                            manager.updateName(id, this@apply.text)
                            blueprintToSave = null
                        }
                    }
                }

                this.add(SquareLabel(manager.getBlueprintWithId(id).sprite, manager::updateIcon))
                this.add(SquareLabel(getIcon("delete_icon", manager.javaClass), manager::deleteElement))
            }
        }

        /**
         * Display a character and its options
         */
        private inner class CharacterPanel(id: Int, name: String) : ItemPanel(id, name) {
            init {
                this.nameLabel.apply {
                    this.isEditable = true
                    this.addFocusLostListener {
                        if (!it.isTemporary) {
                            manager.updateName(id, this@apply.text)
                        }
                    }
                }

                this.add(SquareLabel(transaction { Blueprint[id].HP.toString() }, manager::saveLife))
                this.add(SquareLabel(transaction { Blueprint[id].MP.toString() }, manager::saveMana))
                this.add(SquareLabel(manager.getBlueprintWithId(id).sprite, manager::updateIcon))
                this.add(SquareLabel(getIcon("delete_icon", manager.javaClass), manager::deleteElement))
            }
        }
    }
}