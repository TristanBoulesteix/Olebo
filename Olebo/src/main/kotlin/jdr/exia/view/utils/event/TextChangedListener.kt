package jdr.exia.view.utils.event

import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import javax.swing.text.JTextComponent

fun interface TextChangedListener : DocumentListener {
    override fun changedUpdate(e: DocumentEvent) = warn(e)

    override fun insertUpdate(e: DocumentEvent) = warn(e)

    override fun removeUpdate(e: DocumentEvent) = warn(e)

    fun warn(e: DocumentEvent)
}

fun JTextComponent.addTextChangedListener(textChangedListener: TextChangedListener) =
    document.addDocumentListener(textChangedListener)