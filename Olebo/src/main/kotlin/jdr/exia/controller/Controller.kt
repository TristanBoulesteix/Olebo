package jdr.exia.controller

import jdr.exia.model.act.Act
import jdr.exia.model.dao.DAO

class Controller {
    fun initDatas(): MutableList<Act> {
        return DAO.getAllActs()
    }

    fun start() {

    }
}
