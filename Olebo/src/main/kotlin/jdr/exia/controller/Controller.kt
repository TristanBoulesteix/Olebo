package jdr.exia.controller

import jdr.exia.model.act.Act
import jdr.exia.model.dao.DAO
import jdr.exia.view.ViewManager

class Controller(private val viewManager: ViewManager) {
    fun initDatas(): MutableList<Act> {
        return DAO.getAllActs()
    }

    fun start() {

    }
}
