package jdr.exia.pattern.observer

interface Observable {
    var observer: Observer?

    fun notifyObserver(data : Action) {
        observer?.update(data)
    }
}