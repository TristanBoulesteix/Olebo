package jdr.exia.controller

import jdr.exia.model.dao.DAO
import jdr.exia.pattern.observer.Action
import jdr.exia.pattern.observer.Observable
import jdr.exia.pattern.observer.Observer
import jdr.exia.view.actCreator.ActCreatorDialog

object HomeManager : Observable {
    override var observer: Observer? = null

    fun launchAct(id: Int) {
        notifyObserver(Action.DISPOSE)

        val act = DAO.getActWithId(id)
        TODO("Open act")
    }

    fun openActProperties(id: Int) {
        TODO("Open act properties")
    }

    fun openActCreatorFrame() {
        ActCreatorDialog().isVisible = true
    }

    fun deleteAct(id: Int) {
        DAO.deleteEntity(DAO.getActWithId(id))
        notifyObserver(Action.REFRESH)
    }
}