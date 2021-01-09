package jdr.exia.view.utils.event

import java.awt.Component
import java.awt.event.MouseEvent
import java.awt.event.MouseListener

fun interface MouseReleasedListener : MouseListener {
    override fun mouseClicked(e: MouseEvent) = Unit
    override fun mouseExited(e: MouseEvent) = Unit
    override fun mouseEntered(e: MouseEvent) = Unit
    override fun mousePressed(e: MouseEvent) = Unit
}

fun Component.addMouseReleasedListener(mouseReleasedListener: MouseReleasedListener) =
    this.addMouseListener(mouseReleasedListener)