package jdr.exia.viewModel

import jdr.exia.model.act.Act
import jdr.exia.model.dao.DAO
import jdr.exia.view.frames.editor.elements.BlueprintDialog
import jdr.exia.view.frames.home.panels.ActEditorPanel
import jdr.exia.view.frames.home.panels.ActsPanel
import jdr.exia.viewModel.observer.Action
import jdr.exia.viewModel.observer.Observable
import jdr.exia.viewModel.observer.Observer
import org.jetbrains.exposed.sql.transactions.transaction

object HomeManager : Observable {
    override var observer: Observer? = null

    fun goHome() = notifyObserver(Action.Switch(ActsPanel(this)))

    /**
     * Start an act
     *
     * @param id The id of the act to launch.
     */
    fun launchAct(id: Int) {
        notifyObserver(Action.Dispose)

        transaction {
            ViewManager.initializeAct(Act[id])
        }
    }

    /**
     * Show elements
     */
    fun openObjectEditorFrame() {
        BlueprintDialog().isVisible = true
    }

    /**
     * Show JDialog to create a new act.
     */
    fun openActCreatorFrame() = notifyObserver(Action.Switch(ActEditorPanel(this)))

    /**
     * Show JDialog to update an act.
     *
     * @param id The id of the act to update
     */
    fun updateAct(id: Int) = notifyObserver(Action.Switch(ActEditorPanel(this, DAO.getActWithId(id))))

    /**
     * Show JDialog to delete an act.
     *
     * @param id The id of the act to delete
     */
    fun deleteAct(id: Int) {
        DAO.deleteEntity(DAO.getActWithId(id))
        notifyObserver(Action.Refresh)
    }
}