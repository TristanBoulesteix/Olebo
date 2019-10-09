package jdr.exia.view.event

import java.awt.event.MouseEvent
import java.awt.event.MouseListener

interface ClickListener : MouseListener {
    override fun mouseEntered(e: MouseEvent?) {}
    override fun mouseExited(e: MouseEvent?) {}
    override fun mouseReleased(e: MouseEvent?) {}
    override fun mousePressed(e: MouseEvent?) {}
}