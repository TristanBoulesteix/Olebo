package jdr.exia.view.utils.event

import java.awt.Component
import java.awt.event.MouseEvent

fun interface MousePressedListener : MouseListener {
    override fun mousePressed(e: MouseEvent)
}

fun Component.addMousePressedListener(mousePressedListener: MousePressedListener) =
    this.addMouseListener(mousePressedListener)