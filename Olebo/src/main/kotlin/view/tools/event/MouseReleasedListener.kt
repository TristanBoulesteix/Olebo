package jdr.exia.view.tools.event

import java.awt.Component
import java.awt.event.MouseEvent

fun interface MouseReleasedListener : MouseListener {
    override fun mouseReleased(e: MouseEvent)
}

fun Component.addMouseReleasedListener(mouseReleasedListener: MouseReleasedListener) =
    this.addMouseListener(mouseReleasedListener)