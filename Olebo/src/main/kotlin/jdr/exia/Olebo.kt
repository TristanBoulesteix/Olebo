package jdr.exia

import jdr.exia.controller.Controller
import jdr.exia.view.ViewManager

fun main() {
    val viewManager = ViewManager
    val controller = Controller(viewManager)
    controller.initDatas()
    controller.start()
}

