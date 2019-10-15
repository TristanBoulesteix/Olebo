package jdr.exia.controller

import jdr.exia.model.dao.DAO
import jdr.exia.pattern.observer.Action
import jdr.exia.pattern.observer.Observable
import jdr.exia.pattern.observer.Observer
import jdr.exia.view.editor.acts.ActCreatorDialog

object HomeManager : Observable {
    override var observer: Observer? = null

    /**
     * Start an act
     *
     * @param id The id of the act to launch.
     */
    fun launchAct(id: Int) {
        notifyObserver(Action.DISPOSE)

        val act = DAO.getActWithId(id)
        TODO("Open act")
    }

    /**
     * Show JDialog to create a new act.
     */
    fun openActCreatorFrame() {
        ActCreatorDialog().isVisible = true
        notifyObserver(Action.REFRESH)
    }

    /**
     * Show JDialog to update an act.
     *
     * @param id The id of the act to update
     */
    fun updateAct(id: Int) {
        ActCreatorDialog().fillWithAct(DAO.getActWithId(id)).isVisible = true
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