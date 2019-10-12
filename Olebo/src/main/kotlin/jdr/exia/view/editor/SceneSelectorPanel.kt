package jdr.exia.view.editor

import jdr.exia.controller.ActCreatorManager
import jdr.exia.controller.HomeManager
import jdr.exia.controller.getArrayOfPairs
import jdr.exia.model.utils.getIcon
import jdr.exia.view.template.components.ItemPanel
import jdr.exia.view.template.components.SelectorPanel
import org.jetbrains.exposed.sql.transactions.TransactionManager.Companion.manager
import java.awt.BorderLayout.NORTH
import java.awt.Color
import java.awt.GridBagConstraints
import java.awt.GridBagConstraints.BOTH
import java.awt.GridBagLayout
import javax.swing.BorderFactory
import javax.swing.JPanel

/**
 * This panel contains a JScrollpane which show the list of scenes for the current act in creation
 */
class SceneSelectorPanel(private val controller: ActCreatorManager?) : SelectorPanel() {
    override val pairs: Array<Pair<String, String>>
        get() {
            return controller?.tempScenes?.getArrayOfPairs() ?: arrayOf()
        }

    override fun builder(id: Int, name: String): ItemPanel {
        return ScenePanel(id, name)
    }

    init {
        this.add(JPanel().apply {
            this.layout = GridBagLayout()

            val titleItems = object : ItemPanel(0, "Scènes") {
                init {
                    this.border = BorderFactory.createMatteBorder(2, 2, 0, 2, Color.BLACK)
                    this.add(
                        SquareLabel(
                            getIcon("create_icon", manager.javaClass),
                            controller!!::createNewScene
                        )
                    )
                }
            }
            val cTitleItem = GridBagConstraints().apply {
                this.fill = BOTH
                this.weightx = 1.0
            }

            this.add(titleItems, cTitleItem)
            this.revalidate()
        }, NORTH)
        this.refresh()
    }

    @Suppress("ProtectedInFinal")
    protected inner class ScenePanel(id: Int, name: String) : ItemPanel(id, name) {
        init {
            this.add(SquareLabel(getIcon("edit_icon", HomeManager.javaClass), HomeManager::deleteAct))

            this.add(SquareLabel(getIcon("delete_icon", HomeManager.javaClass), controller!!::deleteNewScene))
        }
    }
}