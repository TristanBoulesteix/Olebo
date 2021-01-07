package jdr.exia.view.utils.components.templates

import jdr.exia.localization.STR_HP
import jdr.exia.localization.STR_MP
import jdr.exia.localization.Strings
import jdr.exia.model.dao.DAO
import jdr.exia.model.element.Element
import jdr.exia.view.utils.IntegerFilter
import org.jetbrains.exposed.sql.transactions.transaction
import java.awt.Font
import java.awt.event.FocusEvent
import java.awt.event.FocusListener
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField
import javax.swing.text.AbstractDocument

class StatsLabel(private val isHP: Boolean, private var maxValue: Int = 0, value: Int = maxValue) : JPanel() {
    private val fontSize = Font("", Font.PLAIN, 15)

    private val statsField = JTextField(value.toString(), 4).apply {
        font = fontSize
        (document as AbstractDocument).documentFilter = IntegerFilter()
        addFocusListener(object : FocusListener {
            override fun focusLost(e: FocusEvent) {
                element?.let {
                    transaction(DAO.database) {
                        if (isHP) {
                            it.currentHealth = extractValue(this@apply.text)
                        } else {
                            it.currentMana = extractValue(this@apply.text)
                        }
                    }
                }
            }

            override fun focusGained(e: FocusEvent) = Unit
        })
        this.isEnabled = false
    }

    private val statsLabelText
        get() = " / $maxValue ${if (isHP) Strings[STR_HP] else Strings[STR_MP]}"

    private val statsLabel = JLabel(statsLabelText).apply {
        font = fontSize
    }

    var element: Element? = null
        set(value) {
            field?.let {
                transaction(DAO.database) {
                    if (isHP) {
                        it.currentHealth = extractValue(statsField.text)
                    } else {
                        it.currentMana = extractValue(statsField.text)
                    }
                }
            }

            val stat = if (value == null) {
                this.maxValue = 0
                this.statsField.isEnabled = false
                0
            } else {
                this.maxValue = if (isHP) value.maxHP else value.maxMana
                this.statsField.isEnabled = true
                if (isHP) value.currentHealth else value.currentMana
            }.toString()
            statsLabel.text = statsLabelText
            statsField.text = stat
            field = value
        }

    init {
        this.add(statsField)
        this.add(statsLabel)
        this.isOpaque = false
    }

    private fun extractValue(string: String) = if (string == "-" || string.isBlank()) 0 else {
        string.toIntOrNull() ?: maxValue
    }.let { if (it > maxValue) maxValue else it }
}