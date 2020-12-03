package view.utils

import javax.swing.text.AttributeSet
import javax.swing.text.DocumentFilter

class IntegerFilter : DocumentFilter() {
    override fun insertString(
        fb: FilterBypass, offset: Int, string: String,
        attr: AttributeSet
    ) {
        val doc = fb.document
        val sb = StringBuilder()
        sb.append(doc.getText(0, doc.length))
        sb.insert(offset, string)
        if (test(sb.toString())) {
            super.insertString(fb, offset, string, attr)
        }
    }

    private fun test(text: String) = text.isEmpty() || text.toIntOrNull() != null

    override fun replace(
        fb: FilterBypass, offset: Int, length: Int, text: String,
        attrs: AttributeSet?
    ) {
        val doc = fb.document
        val sb = StringBuilder()
        sb.append(doc.getText(0, doc.length))
        sb.replace(offset, offset + length, text)
        if (test(sb.toString())) {
            super.replace(fb, offset, length, text, attrs)
        }
    }

    override fun remove(fb: FilterBypass, offset: Int, length: Int) {
        val doc = fb.document
        val sb = StringBuilder()
        sb.append(doc.getText(0, doc.length))
        sb.delete(offset, offset + length)
        if (test(sb.toString())) {
            super.remove(fb, offset, length)
        }
    }
}