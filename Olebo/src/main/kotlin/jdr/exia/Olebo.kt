package jdr.exia

import jdr.exia.controller.Controller
import jdr.exia.view.ViewManager

fun main() {
    Controller(ViewManager).apply {
        this.initDatas()
        this.start()
    }
}

