package view.utils.components

import model.dao.DAO
import model.dao.internationalisation.STR_NO_ELEMENT
import model.dao.internationalisation.Strings
import model.element.Element
import model.utils.isCharacter
import org.jetbrains.exposed.sql.transactions.transaction
import utils.forElse
import view.utils.*
import view.utils.event.ClickListener
import viewModel.pattern.observer.Observable
import viewModel.pattern.observer.Observer
import java.awt.*
import java.awt.event.*
import java.io.File
import java.util.*
import javax.imageio.ImageIO
import javax.swing.*
import javax.swing.border.EmptyBorder
import javax.swing.event.ChangeListener
import javax.swing.text.PlainDocument

/**
 * Template of all JFrame's menu templates
 */
abstract class JFrameTemplate(title: String) : JFrame(),
    Observer {
    protected abstract val observable: Observable

    init {
        this.title = title
        this.minimumSize = DIMENSION_MENU_FRAME
        this.preferredSize = DIMENSION_MENU_FRAME
        this.defaultCloseOperation = DISPOSE_ON_CLOSE
        this.setLocationRelativeTo(null)
        this.layout = BorderLayout()
    }

    override fun dispose() {
        observable.observer = null
        super.dispose()
    }
}

/**
 * Template of all JDialog's menu templates.
 *
 * It is similar to JFrameTemplate because I didn't find a public common parent to JDialog and JFrame.
 */
abstract class JDialogTemplate(title: String, modal: Boolean = true) : JDialog(),
    Observer {
    protected abstract val observable: Observable

    init {
        this.title = title
        if (modal) this.modalityType = ModalityType.APPLICATION_MODAL
        this.minimumSize = DIMENSION_MENU_FRAME
        this.preferredSize = DIMENSION_MENU_FRAME
        this.defaultCloseOperation = DISPOSE_ON_CLOSE
        this.setLocationRelativeTo(null)
        this.layout = BorderLayout()
    }

    override fun createRootPane(): JRootPane {
        return super.createRootPane().apply {
            this.registerKeyboardAction(
                { this@JDialogTemplate.dispose() },
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW
            )
        }
    }

    override fun dispose() {
        observable.observer = null
        super.dispose()
    }
}

/**
 * This panel is a template for all panels which display a list of components
 */
abstract class SelectorPanel : JPanel() {
    protected abstract val pairs: Array<Pair<String, String>>

    protected abstract fun builder(id: Int, name: String): ItemPanel

    init {
        this.background = BACKGROUND_COLOR_LIGHT_BLUE
        this.border = EmptyBorder(20, 20, 20, 20)
        this.layout = BorderLayout()

        this.createJPanelWithItemSelectablePanel()
    }

    private fun createJPanelWithItemSelectablePanel() {
        val panel = JPanel().apply {
            this.border = BorderFactory.createLineBorder(Color.BLACK)
            this.layout = BoxLayout(this, BoxLayout.Y_AXIS)

            pairs.toList().forElse {
                this.add(builder(it.first.toInt(), it.second))
            } ?: this.add(JPanel().apply {
                this.layout = GridBagLayout()
                this.add(JLabel(Strings[STR_NO_ELEMENT]).apply {
                    this.font = Font("Tahoma", Font.BOLD, 20)
                })
            })

            this.addMouseListener(object : MouseAdapter() {
                override fun mouseClicked(e: MouseEvent?) {
                    super.mouseClicked(e)
                    requestFocusInWindow()
                }
            })
        }

        (layout as BorderLayout).getLayoutComponent(BorderLayout.CENTER)?.let {
            this.remove(it)
        }
        this.add(JScrollPane(panel), BorderLayout.CENTER)
    }

    /**
     * Refresh the panel with new datas
     */
    open fun refresh() {
        this.createJPanelWithItemSelectablePanel()
        this.revalidate()
        this.repaint()
    }
}

/**
 * Template for panel which display an item.
 */
@Suppress("LeakingThis")
abstract class ItemPanel(protected val id: Int, name: String) : JPanel() {
    companion object {
        val DIMENSION_SQUARE = Dimension(65, 65)
    }

    protected val nameLabel = JTextField(name).apply {
        this.isEditable = false
        this.isOpaque = false
        this.font = Font("Tahoma", Font.BOLD, 18)
        //this.border = BorderFactory.createEmptyBorder(0, 10, 0, 0)
        this.border = null
        this.background = Color(0, 0, 0, 0)
        this.bounds = Rectangle(0, 10, 0, 0)
        this.disabledTextColor = Color.BLACK
    }

    protected val namePanel = JPanel().apply {
        this.layout = GridBagLayout()
        this.add(
            nameLabel,
            gridBagConstraintsOf(anchor = GridBagConstraints.WEST, fill = GridBagConstraints.BOTH, weightx = 1.0)
        )
    }

    init {
        this.maximumSize = Dimension(Int.MAX_VALUE, 65)
        this.border = BorderFactory.createMatteBorder(0, 0, 2, 0, Color.BLACK)
        this.layout = BoxLayout(this, BoxLayout.X_AXIS)

        this.add(namePanel)
    }

    /**
     * Label that act like a button.
     */
    protected inner class SquareLabel(icon: ImageIcon, action: (Int) -> Unit) :
        JLabel(icon, CENTER) {

        private val listener = object : ClickListener {
            override fun mouseClicked(e: MouseEvent?) {
                action(id)
            }
        }

        init {
            this.preferredSize = DIMENSION_SQUARE
            this.maximumSize = DIMENSION_SQUARE
            this.border = BorderFactory.createMatteBorder(0, 2, 0, 0, Color.BLACK)
            this.addMouseListener(listener)
        }

        constructor(img: String, action: (Int) -> Unit) : this(
            ImageIcon(
                ImageIO.read(File(img)).getScaledInstance(
                    DIMENSION_SQUARE.width, DIMENSION_SQUARE.height, Image.SCALE_SMOOTH
                )
            ), action
        )

        constructor(
            text: String,
            action: ((Int, String) -> Unit)? = null,
            isEditable: Boolean = true
        ) : this(ImageIcon(), { }) {
            this.removeMouseListener(listener)
            this.layout = GridBagLayout()

            this.add(JTextField(text).apply {
                this.isOpaque = false
                this.isEnabled = isEditable
                (this.document as PlainDocument).documentFilter = IntegerFilter()
                this.font = Font("Tahoma", Font.BOLD, 18)
                this.horizontalAlignment = JTextField.CENTER
                this.addFocusListener(object : FocusListener {
                    override fun focusLost(e: FocusEvent) {
                        if (!e.isTemporary) {
                            if (action != null) {
                                action(id, this@apply.text)
                            }
                        }
                    }

                    override fun focusGained(e: FocusEvent?) {}
                })
            }, gridBagConstraintsOf(fill = GridBagConstraints.BOTH, weightx = 1.0, weighty = 1.0))
        }
    }
}

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
                    transaction(DAO.database) {
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

class LabeledItem<T: Component>(label: String, val component: T) : JPanel() {
    init {
        this.add(JLabel(label))
        this.add(component)
    }
}