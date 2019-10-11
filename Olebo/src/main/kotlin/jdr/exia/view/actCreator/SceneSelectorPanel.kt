package jdr.exia.view.actCreator

import jdr.exia.controller.ActCreatorDialogManager
import jdr.exia.controller.HomeFrameManager
import jdr.exia.model.dao.DAO
import jdr.exia.model.utils.getIcon
import jdr.exia.view.template.components.ItemPanel
import jdr.exia.view.template.components.SelectorPanel
import java.awt.BorderLayout.NORTH
import java.awt.GridBagConstraints
import java.awt.GridBagConstraints.BOTH
import java.awt.GridBagLayout
import javax.swing.JPanel

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
                    this.border = null
                    this.add(
                        SquareLabel(
                            getIcon("create_icon", ActCreatorDialogManager.javaClass),
                            HomeFrameManager::deleteAct,
                            false
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
            this.add(SquareLabel(getIcon("edit_icon", HomeFrameManager.javaClass), HomeFrameManager::deleteAct))

            this.add(SquareLabel(getIcon("delete_icon", HomeFrameManager.javaClass), HomeFrameManager::deleteAct))
        }
    }
}