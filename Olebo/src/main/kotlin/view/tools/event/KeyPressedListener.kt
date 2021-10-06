package jdr.exia.view.tools.event

import java.awt.Component
import java.awt.event.KeyEvent
import java.awt.event.KeyListener

fun interface KeyPressedListener : KeyListener {
    override fun keyReleased(e: KeyEvent) = Unit
    override fun keyTyped(e: KeyEvent) = Unit
    override fun keyPressed(e: KeyEvent)
}

fun Component.addKeyPressedListener(keyPressedListener: KeyPressedListener) = this.addKeyListener(keyPressedListener)