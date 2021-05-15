package jdr.exia.view.tools.event

import java.awt.Component
import java.awt.event.MouseEvent

/**
 * Start action on click.
 */
fun interface ClickListener : MouseListener {
    override fun mouseClicked(e: MouseEvent)
}

fun Component.addClickListener(clickListener: ClickListener) = this.addMouseListener(clickListener)