package jdr.exia

import jdr.exia.controller.Controller

fun main() {
    Controller().apply {
        this.initDatas()
        this.start()
    }
}