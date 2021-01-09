package jdr.exia.view.utils.event

import java.awt.Component
import java.awt.event.FocusEvent
import java.awt.event.FocusListener

fun interface FocusLostListener : FocusListener {
    override fun focusGained(e: FocusEvent) = Unit
}

fun Component.addFocusLostListener(focusLostListener: FocusLostListener) = this.addFocusListener(focusLostListener)