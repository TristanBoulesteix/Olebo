package jdr.exia.view.tools.event

import java.awt.Component
import java.awt.event.MouseEvent

fun interface MouseEnteredListener: MouseListener {
    override fun mouseEntered(e: MouseEvent)
}

fun Component.addMouseEnteredListener(mouseEnteredListener: MouseEnteredListener) =
    this.addMouseListener(mouseEnteredListener)