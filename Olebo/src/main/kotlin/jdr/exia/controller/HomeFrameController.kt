package jdr.exia.controller

import jdr.exia.model.dao.DAO
import jdr.exia.view.homeFrame.HomeFrame

object HomeFrameController {
    val frame = HomeFrame()

    fun launchAct(id: Int) {
        frame.dispose()

        val act = DAO.getActWithId(id)
        TODO("Open act")
    }

    fun openActProperties(id: Int) {
        TODO("Open act properties")
    }

    fun deleteAct(id: Int) {
        DAO.deleteEntity(DAO.getActWithId(id))
    }
}