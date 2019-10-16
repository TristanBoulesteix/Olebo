package jdr.exia.view.editor.elements

import jdr.exia.controller.ElementEditorManager
import jdr.exia.controller.HomeManager
import jdr.exia.model.dao.DAO
import jdr.exia.model.element.Blueprint
import jdr.exia.model.element.Type
import jdr.exia.model.utils.getIcon
import jdr.exia.view.utils.components.ItemPanel
import jdr.exia.view.utils.components.SelectorPanel
import org.jetbrains.exposed.sql.transactions.transaction
import java.awt.BorderLayout
import java.awt.Color
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.event.FocusEvent
import java.awt.event.FocusListener
import javax.swing.BorderFactory
import javax.swing.ImageIcon
import javax.swing.JPanel

class ElementSelectorPanel(private val controller: ElementEditorManager?) :
    SelectorPanel() {
    override val pairs: Array<Pair<String, String>>
        get() = controller?.elements?.map { Pair(it.id.value.toString(), it.name) }?.toTypedArray() ?: arrayOf()

    override fun builder(id: Int, name: String): ItemPanel {
        return if(controller?.type != Type.OBJECT) CharacterPanel(id, name) else ObjectPanel(id, name)
    }

    init {
        this.isFocusable = true

        this.add(JPanel().apply {
            this.layout = GridBagLayout()

            val titleItems = object : ItemPanel(0, "Objets") {
                init {
                    this.border = BorderFactory.createMatteBorder(2, 2, 0, 2, Color.BLACK)
                    this.add(
                        SquareLabel(
                            getIcon("create_icon", controller!!.javaClass),
                            HomeManager::updateAct
                        )
                    )
                }
            }
            val cTitleItem = GridBagConstraints().apply {
                this.fill = GridBagConstraints.BOTH
                this.weightx = 1.0
            }

            this.add(titleItems, cTitleItem)
            this.revalidate()
        }, BorderLayout.NORTH)
        this.refresh()
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
                        if(!e.isTemporary) {
                            controller!!.updateName(id, this@apply.text)
                        }
                    }

                    override fun focusGained(e: FocusEvent?) {}
                })
            }

            this.add(SquareLabel(ImageIcon(controller!!.getBlueprintWithId(id).sprite), controller::updateIcon))
            this.add(SquareLabel(getIcon("delete_icon", controller!!.javaClass), controller::deleteElement))
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
                        if(!e.isTemporary) {
                            controller!!.updateName(id, this@apply.text)
                        }
                    }

                    override fun focusGained(e: FocusEvent?) {}
                })
            }

            this.add(SquareLabel(transaction(DAO.database) { Blueprint[id].HP.toString() }, controller!!::saveLife))
            this.add(SquareLabel(transaction(DAO.database) { Blueprint[id].MP.toString() }, controller!!::saveMana))
            this.add(SquareLabel(ImageIcon(controller!!.getBlueprintWithId(id).sprite), controller::updateIcon))
            this.add(SquareLabel(getIcon("delete_icon", controller!!.javaClass), controller::deleteElement))
        }
    }
}