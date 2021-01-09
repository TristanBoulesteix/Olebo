package jdr.exia.view.utils.event

import java.awt.Component
import java.awt.event.MouseEvent
import java.awt.event.MouseListener

fun interface MousePressedListener : MouseListener {
    override fun mouseClicked(e: MouseEvent) = Unit
    override fun mouseExited(e: MouseEvent) = Unit
    override fun mouseEntered(e: MouseEvent) = Unit
    override fun mouseReleased(e: MouseEvent) = Unit
}

fun Component.addMousePressedListener(mousePressedListener: MousePressedListener) =
    this.addMouseListener(mousePressedListener)