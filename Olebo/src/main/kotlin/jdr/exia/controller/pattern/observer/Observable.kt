package jdr.exia.controller.pattern.observer

/**
 * Observable class
 */
interface Observable {
    var observer: Observer?

    fun notifyObserver(data : Action) {
        observer?.update(data)
    }
}