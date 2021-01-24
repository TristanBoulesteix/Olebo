package jdr.exia.view.utils.event

import java.awt.Component
import java.awt.event.FocusEvent
import java.awt.event.FocusListener

fun interface FocusGainedListener : FocusListener {
    override fun focusLost(e: FocusEvent) = Unit
}

fun Component.addFocusGainedListener(focusLostListener: FocusGainedListener) = this.addFocusListener(focusLostListener)