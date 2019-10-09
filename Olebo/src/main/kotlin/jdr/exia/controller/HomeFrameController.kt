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

    val deleteAct= {id: Int ->
        DAO.getActWithId(id).delete()
    }
}