package jdr.exia.controller

import jdr.exia.model.dao.DAO
import jdr.exia.view.acts.ActSelector

class Controller {
    fun initDatas(): Array<String> {
        val acts = DAO.getActsList()

        ActSelector(acts).isVisible = true

        return acts
    }

    fun start() {

    }
}
