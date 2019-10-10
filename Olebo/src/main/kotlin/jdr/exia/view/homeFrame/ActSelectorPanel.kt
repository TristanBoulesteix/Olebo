package jdr.exia.view.homeFrame

import jdr.exia.controller.HomeFrameController
import jdr.exia.model.dao.DAO
import jdr.exia.model.utils.getIcon
import jdr.exia.view.template.components.ItemSelectablePanel
import jdr.exia.view.template.components.SelectorPanel
import jdr.exia.view.template.event.ClickListener
import java.awt.event.MouseEvent

class ActSelectorPanel : SelectorPanel() {
    init {
        this.createJPanelWithItemSelectablePanel(DAO.getActsList()) { id: Int, name: String ->
            ActPanel(id, name)
        }
    }

    private class ActPanel(id: Int, name: String) : ItemSelectablePanel(id, name), ClickListener {
        init {
            this.namePanel.addMouseListener(this@ActPanel)

            this.add(SquareLabel(getIcon("edit_icon", HomeFrameController.javaClass), HomeFrameController::deleteAct))

            this.add(SquareLabel(getIcon("delete_icon", HomeFrameController.javaClass), HomeFrameController::deleteAct))
        }

        override fun mouseClicked(e: MouseEvent?) {
            if (e!!.clickCount == 2) HomeFrameController.launchAct(id)
        }
    }
}