package jdr.exia.view.utils.event

import java.awt.event.FocusEvent
import java.awt.event.FocusListener

fun interface FocusLostListener : FocusListener {
    override fun focusGained(e: FocusEvent) = Unit
}