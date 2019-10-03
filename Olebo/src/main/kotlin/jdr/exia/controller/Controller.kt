package jdr.exia.controller

import jdr.exia.model.dao.DAO

class Controller {
    fun initDatas(): List<String> {
        val acts = DAO.getActsList()

        if(acts.isEmpty()) {
            TODO("Open act creation window")
        }

        return acts
    }

    fun start() {

    }
}
