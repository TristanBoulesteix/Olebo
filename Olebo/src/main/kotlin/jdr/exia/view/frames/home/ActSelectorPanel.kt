package jdr.exia.view.frames.home

import jdr.exia.localization.STR_DOUBLE_CLICK_OPEN_ACT
import jdr.exia.localization.Strings
import jdr.exia.model.dao.DAO
import jdr.exia.model.dao.getIcon
import jdr.exia.view.utils.components.templates.ItemPanel
import jdr.exia.view.utils.components.templates.SelectorPanel
import jdr.exia.view.utils.event.addDoubleClickListener
import jdr.exia.viewModel.HomeManager

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
    protected class ActPanel(id: Int, name: String) : ItemPanel(id, name) {
        init {
            listOf(nameLabel, namePanel).forEach {
                it.addDoubleClickListener { HomeManager.launchAct(id) }
                it.toolTipText = Strings[STR_DOUBLE_CLICK_OPEN_ACT]
            }
            this.nameLabel.isEnabled = false

            this.add(SquareLabel(getIcon("edit_icon", HomeManager.javaClass), HomeManager::updateAct))

            this.add(SquareLabel(getIcon("delete_icon", HomeManager.javaClass), HomeManager::deleteAct))
        }
    }
}