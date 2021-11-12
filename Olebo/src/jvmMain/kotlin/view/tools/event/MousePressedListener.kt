package jdr.exia.view.tools.event

import java.awt.Component
import java.awt.event.MouseEvent

fun interface MousePressedListener : MouseListener {
    override fun mouseClicked(e: MouseEvent)
}

fun Component.addMousePressedListener(mousePressedListener: MousePressedListener) =
    this.addMouseListener(mousePressedListener)