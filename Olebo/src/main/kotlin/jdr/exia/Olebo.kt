package jdr.exia

import jdr.exia.controller.Controller
import jdr.exia.model.Model
import jdr.exia.view.ViewManager

fun main() {
    val model = Model()
    val viewManager = ViewManager
    val controller = Controller(viewManager, model)
    controller.initDatas()
    controller.start()
}

