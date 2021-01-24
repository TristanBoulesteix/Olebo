package jdr.exia.view.utils.components.templates

import java.awt.FlowLayout
import java.awt.event.ActionEvent
import javax.swing.JButton
import javax.swing.JPanel
import javax.swing.JTextField

class ValidableField(private val field: JTextField, onClick: (ActionEvent, String) -> Unit) :
    JPanel(FlowLayout(FlowLayout.CENTER, 0, 0)) {
    private val validationButton = JButton("X").apply {
        addActionListener {
            onClick(it, this@ValidableField.text)
        }
    }

    var text: String by field::text

    init {
        this.isOpaque = false
        this.add(field)
        this.add(validationButton)
    }

    override fun setEnabled(enabled: Boolean) {
        field.isEnabled = enabled
        validationButton.isEnabled = enabled

        if (!enabled)
            field.text = ""

        super.setEnabled(enabled)
    }
}