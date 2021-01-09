package jdr.exia.view.utils.event

import java.awt.event.MouseEvent
import java.awt.event.MouseListener

/**
 * Start action on click.
 */
interface ClickListener : MouseListener {
    override fun mouseEntered(e: MouseEvent) = Unit
    override fun mouseExited(e: MouseEvent) = Unit
    override fun mouseReleased(e: MouseEvent) = Unit
    override fun mousePressed(e: MouseEvent) = Unit
}