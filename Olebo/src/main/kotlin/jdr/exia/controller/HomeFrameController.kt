package jdr.exia.controller

import jdr.exia.model.dao.DAO
import jdr.exia.view.homeFrame.HomeFrame

object HomeFrameController {
    val frame = HomeFrame()

    fun openAct(id: Int) {
        frame.dispose()

        val act = DAO.getActWithId(id)
        TODO("Open act")
    }

    fun deleteAct(id: Int) {
        DAO.deleteEntity(DAO.getActWithId(id))
    }
}