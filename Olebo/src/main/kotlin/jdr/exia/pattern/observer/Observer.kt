package jdr.exia.pattern.observer

import java.awt.Window

interface Observer {
    fun update(data: Action)
}