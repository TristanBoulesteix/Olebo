package view.editor.elements

import model.dao.DAO
import model.element.Blueprint
import model.element.Type
import model.utils.getIcon
import view.utils.components.ItemPanel
import view.utils.components.SelectorPanel
import view.utils.factories.TitlePanel
import view.utils.factories.buildTitleItemPanel
import viewModel.BlueprintManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.awt.BorderLayout
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.event.FocusEvent
import java.awt.event.FocusListener
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
    private val cTitleItem = GridBagConstraints().apply {
        this.fill = GridBagConstraints.BOTH
        this.weightx = 1.0
        this.gridx = 0
        this.gridy = 0
    }

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
    protected inner class ObjectPanel(id: Int, name: String) : ItemPanel(id, name) {
        init {
            this.nameLabel.apply {
                this.isEditable = true
                this.addFocusListener(object : FocusListener {
                    override fun focusLost(e: FocusEvent) {
                        if (!e.isTemporary) {
                            controller!!.updateName(id, this@apply.text)
                        }
                    }

                    override fun focusGained(e: FocusEvent?) {}
                })
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
                this.addFocusListener(object : FocusListener {
                    override fun focusLost(e: FocusEvent) {
                        if (!e.isTemporary) {
                            controller!!.updateName(id, this@apply.text)
                        }
                    }

                    override fun focusGained(e: FocusEvent?) {}
                })
            }

            this.add(SquareLabel(transaction(DAO.database) { Blueprint[id].HP.toString() }, controller!!::saveLife))
            this.add(SquareLabel(transaction(DAO.database) { Blueprint[id].MP.toString() }, controller::saveMana))
            this.add(SquareLabel(controller.getBlueprintWithId(id).sprite, controller::updateIcon))
            this.add(SquareLabel(getIcon("delete_icon", controller.javaClass), controller::deleteElement))
        }
    }
}