package jdr.exia.controller

import jdr.exia.model.dao.DAO
import jdr.exia.view.homeFrame.HomeFrame

object HomeFrameController {
    val frame = HomeFrame()

    fun openAct(id: Int) {
        frame.dispose()

        DAO.getActWithId(id)
        TODO("Open act")
    }
}