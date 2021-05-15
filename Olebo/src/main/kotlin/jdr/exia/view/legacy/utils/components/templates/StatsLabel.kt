package jdr.exia.view.legacy.utils.components.templates

import jdr.exia.localization.STR_HP
import jdr.exia.localization.STR_MP
import jdr.exia.localization.StringLocale
import jdr.exia.model.element.Element
import jdr.exia.model.utils.isCharacter
import jdr.exia.view.legacy.utils.components.filter.IntegerFilter
import jdr.exia.view.tools.event.addTextChangedListener
import org.jetbrains.exposed.sql.transactions.transaction
import java.awt.Color
import java.awt.Font
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField
import javax.swing.text.AbstractDocument

class StatsLabel(private val isHP: Boolean, private var maxValue: Int = 0, value: Int = maxValue) : JPanel() {
    private val fontSize = Font("", Font.PLAIN, 15)

    private val statsField = JTextField(value.toString(), 4).apply {
        font = fontSize
        (document as AbstractDocument).documentFilter = IntegerFilter()
        addTextChangedListener {
            if (element.isCharacter()) {
                transaction {
                    val extracted = extractValue(this@apply.text)

                    foreground = if (extracted !in 0..maxValue) Color.RED else Color.BLACK

                    if (isHP) {
                        element?.currentHealth = extracted
                    } else {
                        element?.currentMana = extracted
                    }
                }
            }
        }
        this.isEnabled = false
    }

    private val statsLabelText
        get() = " / $maxValue ${if (isHP) StringLocale[STR_HP] else StringLocale[STR_MP]}"

    private val statsLabel = JLabel(statsLabelText).apply {
        font = fontSize
    }

    var element: Element? = null
        set(value) {
            field?.let {
                if (it.stillExist() && it.isCharacter())
                    transaction {
                        if (isHP) {
                            it.currentHealth = extractValue(statsField.text)
                        } else {
                            it.currentMana = extractValue(statsField.text)
                        }
                    }
            }

            field = value

            val stat = if (!value.isCharacter()) {
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
        }

    init {
        this.add(statsField)
        this.add(statsLabel)
        this.isOpaque = false
    }

    private fun extractValue(string: String) = if (string == "-" || string.isBlank()) 0 else {
        string.toIntOrNull() ?: maxValue
    }
}