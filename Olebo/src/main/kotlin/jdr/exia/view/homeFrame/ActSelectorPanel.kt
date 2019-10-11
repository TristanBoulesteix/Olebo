package jdr.exia.view.homeFrame

import jdr.exia.controller.HomeFrameManager
import jdr.exia.model.dao.DAO
import jdr.exia.model.utils.getIcon
import jdr.exia.view.template.components.ItemPanel
import jdr.exia.view.template.components.SelectorPanel
import jdr.exia.view.template.event.ClickListener
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

            this.add(SquareLabel(getIcon("edit_icon", HomeFrameManager.javaClass), HomeFrameManager::deleteAct))

            this.add(SquareLabel(getIcon("delete_icon", HomeFrameManager.javaClass), HomeFrameManager::deleteAct))
        }

        override fun mouseClicked(e: MouseEvent?) {
            if (e!!.clickCount == 2) HomeFrameManager.launchAct(id)
        }
    }
}