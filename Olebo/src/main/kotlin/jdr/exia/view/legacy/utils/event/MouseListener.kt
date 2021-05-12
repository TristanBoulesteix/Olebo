package jdr.exia.view.legacy.utils.event

import java.awt.event.MouseEvent
import java.awt.event.MouseListener as MouseListenerSwing

/**
 * This interface is meant to be overloaded to make Kotlin fun interfaces.
 * To do that, the parent interface need to make one function abstract again.
 */
interface MouseListener : MouseListenerSwing {
    override fun mouseEntered(e: MouseEvent) = Unit
    override fun mouseExited(e: MouseEvent) = Unit
    override fun mouseReleased(e: MouseEvent) = Unit
    override fun mousePressed(e: MouseEvent) = Unit
    override fun mouseClicked(e: MouseEvent) = Unit
}