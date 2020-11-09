package viewModel.pattern.observer

/**
 * Observer class
 */
interface Observer {
    fun update(data: Action)
}