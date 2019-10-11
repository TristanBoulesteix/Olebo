package jdr.exia.pattern.observer

import java.awt.Window

interface Observable {
    var observer: Observer?

    fun notifyObserver(data : Action) : Window? {
        return observer?.update(data)
    }
}