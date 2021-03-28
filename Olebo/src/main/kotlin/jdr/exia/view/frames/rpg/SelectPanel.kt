package jdr.exia.view.frames.rpg

import jdr.exia.localization.*
import jdr.exia.model.utils.emptyElements
import jdr.exia.view.frames.rpg.modifier.PriorityCombo
import jdr.exia.view.frames.rpg.modifier.SideDataPanel
import jdr.exia.view.frames.rpg.modifier.SizeCombo
import jdr.exia.view.utils.BACKGROUND_COLOR_SELECT_PANEL
import jdr.exia.view.utils.DEFAULT_INSET
import jdr.exia.view.utils.DIMENSION_BUTTON_DEFAULT
import jdr.exia.view.utils.components.templates.StatsLabel
import jdr.exia.view.utils.gridBagConstraintsOf
import jdr.exia.viewModel.ViewManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.awt.*
import javax.swing.JButton
import javax.swing.JPanel


/**
 * Contains all this info regarding the item selected by the Game Master.
 *
 * This is a singleton.
 */
class SelectPanel : JPanel() {
    private val sidePanel = SideDataPanel()

    private val rotateRightButton = JButton(StringLocale[STR_ROTATE_TO_RIGHT]).apply {
        preferredSize = DIMENSION_BUTTON_DEFAULT
        addActionListener {
            ViewManager.rotateRight()
        }
    }

    private val rotateLeftButton = JButton(StringLocale[STR_ROTATE_TO_LEFT]).apply {
        preferredSize = DIMENSION_BUTTON_DEFAULT
        addActionListener {
            ViewManager.rotateLeft()
        }
    }

    private val visibilityButton = object : JButton() { //Toggles visibility on selected Token
        private val defaultText by StringDelegate(STR_VISIBILITY)

        private val conditionVisibility
            get() = selectedElements.filter { it.isVisible }.size >= selectedElements.size / 2

        private val contentText
            get() = when {
                selectedElements.isEmpty() -> StringLocale[STR_HIDE]
                selectedElements.size == 1 -> if (selectedElements[0].isVisible) StringLocale[STR_HIDE] else StringLocale[STR_SHOW]
                conditionVisibility -> StringLocale[STR_HIDE]
                else -> StringLocale[STR_SHOW]
            }

        init {
            text = defaultText
            preferredSize = DIMENSION_BUTTON_DEFAULT
            addActionListener {
                this.text = contentText
                val visibility = when {
                    selectedElements.size == 1 -> null
                    selectedElements.isNotEmpty() -> !conditionVisibility
                    else -> null
                }
                ViewManager.toggleVisibility(selectedElements, visibility)
            }
        }

        fun initialize(turnOff: Boolean) {
            if (turnOff) {
                text = defaultText
                isEnabled = false
            } else {
                text = contentText
                isEnabled = true
            }
        }
    }


    private val deleteButton = JButton(StringLocale[STR_DELETE]).apply { //Deletes selected Token
        preferredSize = DIMENSION_BUTTON_DEFAULT
        addActionListener {
            ViewManager.removeElements(selectedElements)
            ViewManager.repaint()
        }
    }

    private val lifeField = StatsLabel(true)

    private val manaField = StatsLabel(false)

    var selectedElements = emptyElements()

    init {
        val insets = Insets(5, 5, 5, 5)

        this.layout = GridBagLayout()
        this.preferredSize = Dimension(500, 10)

        this.add(
            sidePanel, gridBagConstraintsOf(
                0,
                0,
                weightx = .5,
                insets = DEFAULT_INSET,
                anchor = GridBagConstraints.LINE_START,
                fill = GridBagConstraints.VERTICAL,
                gridHeight = 3
            )
        )

        this.add(
            rotateRightButton, gridBagConstraintsOf(
                1,
                0,
                weightx = .5,
                insets = insets,
                anchor = GridBagConstraints.LINE_START
            )
        )

        this.add(
            rotateLeftButton, gridBagConstraintsOf(
                1,
                2,
                weightx = .5,
                insets = insets,
                anchor = GridBagConstraints.LINE_START
            )
        )

        this.add(
            visibilityButton, gridBagConstraintsOf(
                3,
                0,
                weightx = .5,
                insets = insets,
                anchor = GridBagConstraints.LINE_START
            )
        )

        this.add(
            deleteButton, gridBagConstraintsOf(
                3,
                2,
                weightx = .5,
                insets = Insets(10, 10, 10, 10),
                anchor = GridBagConstraints.LINE_START
            )
        )

        this.add(
            lifeField, gridBagConstraintsOf(
                4,
                0,
                weightx = .5,
                insets = Insets(10, 10, 10, 10),
                anchor = GridBagConstraints.LINE_START
            )
        )

        this.add(
            manaField, gridBagConstraintsOf(
                4,
                2,
                weightx = .5,
                insets = Insets(10, 10, 10, 10),
                anchor = GridBagConstraints.LINE_START
            )
        )

        this.background = BACKGROUND_COLOR_SELECT_PANEL
    }

    fun reload() {
        with(selectedElements) {

            sidePanel.priorityCombo = PriorityCombo(this)
            sidePanel.sizeCombo = SizeCombo(this)

            sidePanel.nameLabel.isEnabled = false

            if (this.isNotEmpty()) {
                arrayOf(rotateRightButton, rotateLeftButton, deleteButton).forEach { it.isEnabled = true }

                visibilityButton.initialize(false)
                sidePanel.blueprintNameLabel.text =
                    if (this.size == 1) this[0].name else "$size ${StringLocale[STR_SELECTED_ELEMENTS, StringStates.NORMAL]}"

                if (this.size == 1) {
                    lifeField.element = this[0]
                    manaField.element = this[0]
                    sidePanel.nameLabel.let {
                        it.text = transaction { this@with[0].alias }
                        it.isEnabled = true
                    }
                }
            } else {
                sidePanel.blueprintNameLabel.text = null

                arrayOf(rotateRightButton, rotateLeftButton, deleteButton).forEach {
                    it.isEnabled = false
                }

                visibilityButton.initialize(true)

                lifeField.element = null
                manaField.element = null
            }
        }

        sidePanel.reload()
        revalidate()
        repaint()
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)

        with(selectedElements) {
            g.color = if (this.isEmpty() || this.all { it.isVisible }) {
                Color.BLACK
            } else {
                Color.BLUE
            }
            g.fillRect(15, 30, 110, 110)

            if (this.size == 1) {
                g.drawImage(this[0].sprite.image, 20, 30, 100, 100, null)
            } else {
                g.color = Color.WHITE
                g.fillRect(20, 35, 100, 100)
            }
        }
    }
}