package jdr.exia.view.tools.event

import java.awt.Component
import java.awt.event.MouseEvent
import java.awt.event.MouseListener

inline fun Component.addMouseReleasedListener(crossinline mouseReleasedListener: (e: MouseEvent) -> Unit) =
    this.addMouseListener(object: MouseListener {
        override fun mouseClicked(e: MouseEvent?) = Unit

        override fun mousePressed(e: MouseEvent?) = Unit

        override fun mouseReleased(e: MouseEvent) = mouseReleasedListener(e)

        override fun mouseEntered(e: MouseEvent?) = Unit

        override fun mouseExited(e: MouseEvent?) = Unit
    })