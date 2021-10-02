package jdr.exia.view.tools.event

import java.awt.Component
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent

inline fun Component.addMouseExitedListener(crossinline action: (MouseEvent) -> Unit) =
    this.addMouseListener(object : MouseAdapter() {
        override fun mouseExited(me: MouseEvent) = action(me)
    })

inline fun Component.addMouseMovedListener(crossinline action: (MouseEvent) -> Unit) =
    this.addMouseMotionListener(object : MouseAdapter() {
        override fun mouseMoved(me: MouseEvent) = action(me)
    })