package jdr.exia.viewModel.observer

/**
 * Observable class
 */
interface Observable {
    var observer: Observer?

    fun notifyObserver(data: Action) {
        observer?.update(data)
    }
}