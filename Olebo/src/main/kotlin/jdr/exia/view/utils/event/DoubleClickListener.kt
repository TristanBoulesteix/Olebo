package jdr.exia.view.utils.event

import java.awt.Component
import java.awt.event.MouseEvent
import javax.swing.SwingUtilities

fun interface DoubleClickListener : ClickListener {
    /**
     * To make the double click event works, this method should not be overloaded or it must call super [mouseClicked].
     */
    override fun mouseClicked(e: MouseEvent) {
        if (e.clickCount == 2 && SwingUtilities.isLeftMouseButton(e))
            mouseDoubleClicked(e)
    }

    fun mouseDoubleClicked(e: MouseEvent)
}

fun Component.addDoubleClickListener(doubleClickListener: DoubleClickListener) = this.addMouseListener(doubleClickListener)