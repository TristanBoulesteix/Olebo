package jdr.exia.view.frames.editor.elements

import jdr.exia.model.dao.getIcon
import jdr.exia.model.element.Blueprint
import jdr.exia.model.element.Type
import jdr.exia.view.utils.components.templates.ItemPanel
import jdr.exia.view.utils.components.templates.SelectorPanel
import jdr.exia.view.utils.event.addFocusLostListener
import jdr.exia.view.utils.factories.TitlePanel
import jdr.exia.view.utils.factories.buildTitleItemPanel
import jdr.exia.view.utils.gridBagConstraintsOf
import jdr.exia.viewModel.BlueprintManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.awt.BorderLayout
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.JPanel

/**
 * Panel to select elements. Show a list of blueprints
 *
 * @param controller
 */
class ElementSelectorPanel(private val controller: BlueprintManager?) :
    SelectorPanel() {
    override val pairs: Array<Pair<String, String>>
        get() = controller?.elements?.map { Pair(it.id.value.toString(), it.name) }?.toTypedArray() ?: arrayOf()

    override fun builder(id: Int, name: String): ItemPanel {
        return if (controller?.type != Type.OBJECT) CharacterPanel(id, name) else ObjectPanel(id, name)
    }

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

        this.add(titlePanel.apply {
            this.layout = GridBagLayout()

            titleContentPanel = buildTitleItemPanel(controller!!)
            this.add(titleContentPanel, cTitleItem)
            this.revalidate()
        }, BorderLayout.NORTH)
        this.refresh()
    }

    override fun refresh() {
        titlePanel.remove(titleContentPanel)
        titleContentPanel = buildTitleItemPanel(controller!!)
        titlePanel.add(titleContentPanel, cTitleItem)
        super.refresh()
    }

    /**
     * Display an object and its options
     */
    @Suppress("ProtectedInFinal")
    private inner class ObjectPanel(id: Int, name: String) : ItemPanel(id, name) {
        init {
            this.nameLabel.apply {
                this.isEditable = true
                this.addFocusLostListener {
                    if (!it.isTemporary) {
                        controller!!.updateName(id, this@apply.text)
                    }
                }
            }

            this.add(SquareLabel(controller!!.getBlueprintWithId(id).sprite, controller::updateIcon))
            this.add(SquareLabel(getIcon("delete_icon", controller.javaClass), controller::deleteElement))
        }
    }

    /**
     * Display a character and its options
     */
    @Suppress("ProtectedInFinal")
    protected inner class CharacterPanel(id: Int, name: String) : ItemPanel(id, name) {
        init {
            this.nameLabel.apply {
                this.isEditable = true
                this.addFocusLostListener {
                    if (!it.isTemporary) {
                        controller!!.updateName(id, this@apply.text)
                    }
                }
            }

            this.add(SquareLabel(transaction { Blueprint[id].HP.toString() }, controller!!::saveLife))
            this.add(SquareLabel(transaction { Blueprint[id].MP.toString() }, controller::saveMana))
            this.add(SquareLabel(controller.getBlueprintWithId(id).sprite, controller::updateIcon))
            this.add(SquareLabel(getIcon("delete_icon", controller.javaClass), controller::deleteElement))
        }
    }
}