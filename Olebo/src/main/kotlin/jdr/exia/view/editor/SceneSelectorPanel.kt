package jdr.exia.view.editor

import jdr.exia.controller.ActCreatorManager
import jdr.exia.controller.HomeManager
import jdr.exia.model.dao.DAO
import jdr.exia.model.utils.getIcon
import jdr.exia.view.template.components.ItemPanel
import jdr.exia.view.template.components.SelectorPanel
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
class SceneSelectorPanel : SelectorPanel() {
    override val pairs: Array<Pair<String, String>>
        get() = DAO.getActsList()

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
                            getIcon("create_icon", ActCreatorManager.javaClass),
                            ActCreatorManager::createNewScene
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
    }

    @Suppress("ProtectedInFinal")
    protected class ScenePanel(id: Int, name: String) : ItemPanel(id, name) {
        init {
            this.add(SquareLabel(getIcon("edit_icon", HomeManager.javaClass), HomeManager::deleteAct))

            this.add(SquareLabel(getIcon("delete_icon", HomeManager.javaClass), HomeManager::deleteAct))
        }
    }
}