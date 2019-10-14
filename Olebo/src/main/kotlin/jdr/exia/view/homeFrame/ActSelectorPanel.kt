package jdr.exia.view.homeFrame

import jdr.exia.controller.HomeManager
import jdr.exia.model.dao.DAO
import jdr.exia.model.utils.getIcon
import jdr.exia.view.utils.components.ItemPanel
import jdr.exia.view.utils.components.SelectorPanel
import jdr.exia.view.utils.event.ClickListener
import java.awt.event.MouseEvent

class ActSelectorPanel : SelectorPanel() {
    override val pairs
        get() = DAO.getActsList()

    override fun builder(id: Int, name: String): ItemPanel {
        return ActPanel(id, name)
    }

    @Suppress("ProtectedInFinal")
    protected class ActPanel(id: Int, name: String) : ItemPanel(id, name), ClickListener {
        init {
            this.namePanel.addMouseListener(this)

            this.add(SquareLabel(getIcon("edit_icon", HomeManager.javaClass), HomeManager::updateAct))

            this.add(SquareLabel(getIcon("delete_icon", HomeManager.javaClass), HomeManager::deleteAct))
        }

        override fun mouseClicked(e: MouseEvent?) {
            if (e!!.clickCount == 2) HomeManager.launchAct(id)
        }
    }
}