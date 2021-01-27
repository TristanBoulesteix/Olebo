package jdr.exia.view.utils.components.templates

import jdr.exia.model.dao.DAO
import jdr.exia.model.element.Element
import jdr.exia.model.utils.isCharacter
import jdr.exia.view.utils.BACKGROUND_COLOR_SELECT_PANEL
import jdr.exia.view.utils.MARGIN_LEFT
import jdr.exia.view.utils.gridBagConstraintsOf
import org.jetbrains.exposed.sql.transactions.transaction
import java.awt.*
import java.util.*
import javax.swing.*
import javax.swing.event.ChangeListener

class SlideStats(private val hp: Boolean, initialElement: Element? = null) : JPanel() {
    private val label: JTextField
    private val slider: JSlider

    private val statName
        get() = if (hp) "PV" else "PM"

    private var eventAction: ChangeListener

    var element: Element? = null
        set(value) {
            field = value

            if (value == null || !value.isCharacter()) {
                label.text = "X / X"
                label.disabledTextColor = Color(109, 109, 109)
                slider.isEnabled = false
            } else {
                label.text = if (hp)
                    "${value.currentHealth} / ${value.maxHP}"
                else
                    "${value.currentMana} / ${value.maxMana}"
                label.disabledTextColor = Color.BLACK
                slider.apply {
                    this.maximum = if (hp) value.maxHP else value.maxMana
                    this.value = if (hp) value.currentHealth else value.currentMana
                    this.isEnabled = true
                }
            }
        }

    init {
        layout = GridBagLayout()
        border = MARGIN_LEFT
        applyStyle()

        label = object : JTextField() {
            init {
                text = "5"
                preferredSize = Dimension(80, 30)
                isEnabled = false
                disabledTextColor = Color.BLACK
                border = null
                applyStyle()
            }

            override fun setText(t: String) {
                super.setText("$statName : $t")
            }
        }

        slider = object : JSlider() {
            private val basePositions = Hashtable(
                mapOf(
                    -20 to JLabel("-20"),
                    0 to JLabel("0")
                )
            )

            init {
                applyStyle()
                maximum = 20
                minimum = -20
                value = 0
                minorTickSpacing = 1
                majorTickSpacing = 5
                paintLabels = true
                paintTicks = true

                eventAction = ChangeListener {
                    transaction {
                        element?.let {
                            if (hp) it.currentHealth = value else it.currentMana = value
                            label.text = if (hp)
                                "${it.currentHealth} / ${it.maxHP}"
                            else
                                "${it.currentMana} / ${it.maxMana}"
                        }
                    }
                }
            }

            fun removeListeners() = changeListeners.forEach {
                removeChangeListener(it)
            }

            override fun setEnabled(enabled: Boolean) {
                if (element == null && !enabled) {
                    removeListeners()
                    maximum = 20
                    value = 0
                } else {
                    addChangeListener(eventAction)
                }

                super.setEnabled(enabled)
            }

            override fun setMaximum(maximum: Int) {
                labelTable = Hashtable(mapOf(maximum to JLabel("$maximum"))).also { it += basePositions }
                removeListeners()
                super.setMaximum(maximum)
                addChangeListener(eventAction)
            }
        }

        this.add(
            label, gridBagConstraintsOf(
                anchor = GridBagConstraints.LINE_START,
                gridx = 0,
                gridy = 0,
                insets = Insets(2, 0, 0, 0)
            )
        )

        this.add(
            slider, gridBagConstraintsOf(
                anchor = GridBagConstraints.LAST_LINE_END,
                gridx = 1,
                gridy = 0,
                fill = GridBagConstraints.BOTH,
                weightx = 1.0
            )
        )

        element = initialElement
    }

    private fun JComponent.applyStyle() {
        background = BACKGROUND_COLOR_SELECT_PANEL
        isOpaque = false
    }
}