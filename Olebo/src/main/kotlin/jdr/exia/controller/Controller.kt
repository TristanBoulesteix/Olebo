package jdr.exia.controller

import jdr.exia.model.Model
import jdr.exia.view.ViewManager

class Controller(private val viewManager: ViewManager, private val model: Model) {
    fun initDatas() {
        model.loadDatabase()
    }

    fun start() {

    }
}
