package jdr.exia.viewModel

import jdr.exia.model.act.Act
import jdr.exia.model.dao.DAO
import org.jetbrains.exposed.sql.transactions.transaction
import jdr.exia.view.frames.editor.acts.ActEditorDialog
import jdr.exia.view.frames.editor.elements.BlueprintDialog
import jdr.exia.viewModel.pattern.observer.Action
import jdr.exia.viewModel.pattern.observer.Observable
import jdr.exia.viewModel.pattern.observer.Observer

object HomeManager : Observable {
    override var observer: Observer? = null

    /**
     * Start an act
     *
     * @param id The id of the act to launch.
     */
    fun launchAct(id: Int) {
        notifyObserver(Action.DISPOSE)

        transaction(DAO.database) {
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
    fun openActCreatorFrame() {
        ActEditorDialog().isVisible = true
        notifyObserver(Action.REFRESH)
    }

    /**
     * Show JDialog to update an act.
     *
     * @param id The id of the act to update
     */
    fun updateAct(id: Int) {
        ActEditorDialog().fillWithAct(DAO.getActWithId(id)).isVisible = true
        notifyObserver(Action.REFRESH)
    }

    /**
     * Show JDialog to delete an act.
     *
     * @param id The id of the act to delete
     */
    fun deleteAct(id: Int) {
        DAO.deleteEntity(DAO.getActWithId(id))
        notifyObserver(Action.REFRESH)
    }
}