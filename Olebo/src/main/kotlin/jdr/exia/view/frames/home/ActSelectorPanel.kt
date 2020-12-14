package jdr.exia.view.frames.home

import jdr.exia.model.dao.DAO
import jdr.exia.model.dao.getIcon
import jdr.exia.localization.STR_DOUBLE_CLICK_OPEN_ACT
import jdr.exia.localization.Strings
import jdr.exia.view.utils.components.ItemPanel
import jdr.exia.view.utils.components.SelectorPanel
import jdr.exia.view.utils.event.ClickListener
import jdr.exia.viewModel.HomeManager
import java.awt.event.MouseEvent

/**
 * This panel's goals is to display all acts in the database
 */
class ActSelectorPanel : SelectorPanel() {
    override val pairs
        get() = DAO.getActsList()

    override fun builder(id: Int, name: String): ItemPanel {
        return ActPanel(id, name)
    }

    /**
     * This panel display an Act
     */
    @Suppress("ProtectedInFinal")
    protected class ActPanel(id: Int, name: String) : ItemPanel(id, name), ClickListener {
        init {
            listOf(nameLabel, namePanel).forEach {
                it.addMouseListener(this)
                it.toolTipText = Strings[STR_DOUBLE_CLICK_OPEN_ACT]
            }
            this.nameLabel.isEnabled = false

            this.add(SquareLabel(getIcon("edit_icon", HomeManager.javaClass), HomeManager::updateAct))

            this.add(SquareLabel(getIcon("delete_icon", HomeManager.javaClass), HomeManager::deleteAct))
        }

        override fun mouseClicked(e: MouseEvent?) {
            if (e!!.clickCount == 2) HomeManager.launchAct(id)
        }
    }
}