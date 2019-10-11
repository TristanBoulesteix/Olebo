package jdr.exia.controller

import jdr.exia.model.dao.DAO
import jdr.exia.pattern.Action
import jdr.exia.pattern.Observable
import jdr.exia.pattern.Observer

object HomeFrameController : Observable {
    override var observer: Observer? = null

    fun launchAct(id: Int) {
        notifyObserver(Action.DISPOSE)

        val act = DAO.getActWithId(id)
        TODO("Open act")
    }

    fun openActProperties(id: Int) {
        TODO("Open act properties")
    }

    fun deleteAct(id: Int) {
        DAO.deleteEntity(DAO.getActWithId(id))
        notifyObserver(Action.REFRESH)
    }
}