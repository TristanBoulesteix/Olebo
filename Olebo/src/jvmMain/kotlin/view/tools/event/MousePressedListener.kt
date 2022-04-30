package jdr.exia.view.tools.event

import java.awt.Component
import java.awt.event.MouseEvent
import java.awt.event.MouseListener

inline fun Component.addMousePressedListener(crossinline mousePressedListener: (e: MouseEvent) -> Unit): MouseListener {
    val listener = object: MouseListener {
        override fun mouseClicked(e: MouseEvent?) = Unit

        override fun mousePressed(e: MouseEvent) = mousePressedListener(e)

        override fun mouseReleased(e: MouseEvent?) = Unit

        override fun mouseEntered(e: MouseEvent?) = Unit

        override fun mouseExited(e: MouseEvent?) = Unit
    }

    this.addMouseListener(listener)

    return listener
}

fun Component.removeMousePressedListener(mousePressedListener: MouseListener) =
    this.removeMouseListener(mousePressedListener)