package jdr.exia.view.legacy.utils.components.filter

import javax.swing.JTextField
import javax.swing.text.AttributeSet
import javax.swing.text.DocumentFilter

/**
 * Number of Character maximum allowed in a [JTextField]
 *
 * @param limit The maximum number of characters allowed
 */
class MaxCharFilter(limit: Int) : DocumentFilter() {
    private val limit: Int

    init {
        if (limit <= 0) throw IllegalArgumentException("The limit can't be <= 0.")
        this.limit = limit
    }

    override fun replace(fb: FilterBypass, offset: Int, length: Int, text: String, attrs: AttributeSet?) {
        if (fb.document.length + text.length - limit - length <= 0)
            super.replace(fb, offset, length, text, attrs)
    }
}