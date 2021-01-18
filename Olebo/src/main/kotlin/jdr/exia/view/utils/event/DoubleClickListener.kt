package jdr.exia.view.utils.event

import java.awt.Component
import java.awt.event.MouseEvent
import javax.swing.SwingUtilities

inline fun Component.addDoubleClickListener(crossinline doubleClickEvent: (MouseEvent) -> Unit) =
    this.addClickListener { e ->
        if (e.clickCount == 2 && SwingUtilities.isLeftMouseButton(e))
            doubleClickEvent(e)
    }