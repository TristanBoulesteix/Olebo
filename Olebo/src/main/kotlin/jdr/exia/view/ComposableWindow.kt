package jdr.exia.view

import jdr.exia.view.ui.defaultCursor
import jdr.exia.view.ui.handCursor
import javax.swing.JFrame

@Suppress("")
abstract class ComposableWindow(title: String) : JFrame(title) {
    companion object {
        private val composableWindows = mutableListOf<ComposableWindow>()

        val currentFocused
            get() = composableWindows.find { it.isFocused }
    }

    private var focusRequestedNumber = 0
        set(value) {
            field = value

            if (field < 0) field = 0
        }

    fun hasItemFocus(requestFocus: Boolean) {
        if (requestFocus) focusRequestedNumber++ else focusRequestedNumber--

        cursor = if (focusRequestedNumber > 0) handCursor else defaultCursor
    }

    override fun setVisible(isVisible: Boolean) {
        if (isVisible) {
            composableWindows += this
        } else {
            composableWindows -= this
        }
        super.setVisible(isVisible)
    }

    override fun dispose() {
        composableWindows -= this
        super.dispose()
    }
}