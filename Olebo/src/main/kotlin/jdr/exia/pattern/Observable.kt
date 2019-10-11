package jdr.exia.pattern

interface Observable {
    var observer: Observer?

    fun notifyObserver(data : Action) {
        observer?.update(data)
    }
}