package jdr.exia.viewModel.observer

import jdr.exia.view.frames.home.panels.HomePanel

/**
 * Action the controler can send to the frame
 */
sealed class Action {
    /**
     * Dispose the [javax.swing.JFrame]
     */
    object Dispose : Action()

    /**
     * Refresh everything that need to be updated
     */
    object Refresh : Action()

    /**
     * Switch [HomePanel]
     */
    class Switch(val panel: HomePanel) : Action()
}