package view.frames.rpg

import model.element.Element
import model.element.Priority
import model.element.Size
import model.internationalisation.*
import model.utils.callManager
import view.utils.BACKGROUND_COLOR_SELECT_PANEL
import view.utils.DIMENSION_BUTTON_DEFAULT
import view.utils.applyAndAppendTo
import view.utils.components.SlideStats
import view.utils.gridBagConstraintsOf
import viewModel.ViewManager
import java.awt.*
import javax.swing.*
import javax.swing.border.EmptyBorder


/**
 * Contains all this info regarding the item selected by the Game Master.
 * This is a singleton.
 */
object SelectPanel : JPanel() {
    var selectedElements = listOf<Element>()

    private val slidePanel: JPanel

    private val lifeSlide = SlideStats(true)

    private val manaSlide = SlideStats(false)

    private val nameLabel = object : JLabel(Strings[STR_NAME]) {
        init {
            horizontalTextPosition = CENTER
            border = EmptyBorder(20, 0, 0, 0)
        }

        override fun setText(text: String?) {
            if (text == null) {
                this.isEnabled = false
                super.setText(Strings[STR_NAME])
            } else {
                this.isEnabled = true
                super.setText(text)
            }
        }
    }

    private val rotateRightButton = JButton(Strings[STR_ROTATE_TO_RIGHT]).apply {
        preferredSize = DIMENSION_BUTTON_DEFAULT
        addActionListener {
            ViewManager.rotateRight()
        }
    }

    private val rotateLeftButton = JButton(Strings[STR_ROTATE_TO_LEFT]).apply {
        preferredSize = DIMENSION_BUTTON_DEFAULT
        addActionListener {
            ViewManager.rotateLeft()
        }
    }

    private val priorityHighButton: JRadioButton
    private val priorityRegularButton: JRadioButton
    private val priorityLowButton: JRadioButton
    private val priorityInvisibleButton: JRadioButton

    private val visibilityButton = object : JButton() { //Toggles visibility on selected Token
        private val defaultText by StringDelegate(STR_VISIBILITY)

        private val conditionVisibility
            get() = selectedElements.filter { it.isVisible }.size >= selectedElements.size / 2

        private val contentText
            get() = when {
                selectedElements.isEmpty() -> Strings[STR_HIDE]
                selectedElements.size == 1 -> if (selectedElements[0].isVisible) Strings[STR_HIDE] else Strings[STR_SHOW]
                conditionVisibility -> Strings[STR_HIDE]
                else -> Strings[STR_SHOW]
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

    private val deleteButton = JButton(Strings[STR_DELETE]).apply { //Deletes selected Token
        preferredSize = DIMENSION_BUTTON_DEFAULT
        addActionListener {
            selectedElements.forEach(ViewManager::removeToken)
            ViewManager.repaint()
        }
    }

    private val sizeCombo = object : JComboBox<String>(arrayOf("XS", "S", "M", "L", "XL", "XXL")) {
        init {
            addActionListener { _ ->
                with(selectedElements.filter { it.size != selectedItem }) {
                    val newSize = when (selectedItem) {
                        "XS" -> Size.XS
                        "S" -> Size.S
                        "M" -> Size.M
                        "L" -> Size.L
                        "XL" -> Size.XL
                        "XXL" -> Size.XXL
                        else -> Size.DEFAULT
                    }
                    ViewManager.activeScene.callManager(newSize, this, Element::cmdDimension)
                }
                ViewManager.repaint()
            }
            border = EmptyBorder(0, 0, 0, 0)
        }

        override fun setSelectedItem(selected: Any?) {
            if (selected == null) {
                this.isEnabled = false
                super.setSelectedItem("S")
            } else {
                this.isEnabled = true
                super.setSelectedItem(if (selected is List<*> && selected[0] is Element) (selected[0] as Element).size.name else if (selected is String) selected else "S")
            }
        }
    }

    init {
        this.layout = GridBagLayout()
        this.preferredSize = Dimension(500, 10)

        val inset = Insets(5, 5, 5, 5)

        this.add(
            nameLabel, gridBagConstraintsOf(
                0,
                0,
                weightx = 1.0,
                insets = Insets(10, 150, 10, 10),
                anchor = GridBagConstraints.LINE_START
            )
        )

        this.add(
            sizeCombo, gridBagConstraintsOf(
                0,
                2,
                weightx = 1.0,
                insets = Insets(10, 150, 10, 10),
                anchor = GridBagConstraints.LINE_START
            )
        )

        this.add(
            rotateRightButton, gridBagConstraintsOf(
                1,
                0,
                weightx = .5,
                insets = inset,
                anchor = GridBagConstraints.LINE_START
            )
        )

        this.add(
            rotateLeftButton, gridBagConstraintsOf(
                1,
                2,
                weightx = .5,
                insets = inset,
                anchor = GridBagConstraints.LINE_START
            )
        )

        ButtonGroup().apply {
            priorityHighButton = JRadioButton(Strings[STR_FOREGROUND]).apply {
                this.addActionListener {
                    ViewManager.updatePriorityToken(Priority.HIGH)
                }
            }
            priorityRegularButton = JRadioButton(Strings[STR_DEFAULT]).apply {
                this.addActionListener {
                    ViewManager.updatePriorityToken(Priority.REGULAR)
                }
            }
            priorityLowButton = JRadioButton(Strings[STR_BACKGROUND]).apply {
                this.addActionListener {
                    ViewManager.updatePriorityToken(Priority.LOW)
                }
            }
            priorityInvisibleButton = JRadioButton().apply {
                this.isSelected = true
            }

            arrayOf(priorityHighButton, priorityRegularButton, priorityLowButton, priorityInvisibleButton).forEach {
                this.add(it)
                it.isEnabled = false
                it.isOpaque = false
            }
        }

        this.add(
            priorityHighButton, gridBagConstraintsOf(
                2,
                0,
                weightx = .5,
                weighty = 1.0,
                insets = inset,
                anchor = GridBagConstraints.LINE_START
            )
        )

        this.add(
            priorityRegularButton, gridBagConstraintsOf(
                2,
                1,
                weightx = .5,
                weighty = 1.0,
                insets = inset,
                anchor = GridBagConstraints.LINE_START
            )
        )

        this.add(
            priorityLowButton, gridBagConstraintsOf(
                2,
                2,
                weightx = .5,
                weighty = 1.0,
                insets = inset,
                anchor = GridBagConstraints.LINE_START
            )
        )

        this.add(
            visibilityButton, gridBagConstraintsOf(
                3,
                0,
                weightx = .5,
                insets = inset,
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

        slidePanel = JPanel().applyAndAppendTo(
            this, gridBagConstraintsOf(
                4,
                0,
                gridHeight = 3,
                weightx = 2.0,
                insets = Insets(10, 10, 10, 10),
                anchor = GridBagConstraints.FIRST_LINE_START,
                fill = GridBagConstraints.BOTH
            )
        ) {
            isOpaque = false
            layout = GridLayout(2, 1)

            add(lifeSlide)
            add(manaSlide)
        }

        this.background = BACKGROUND_COLOR_SELECT_PANEL
    }

    fun reload() {
        val priorityRadioButtons = arrayOf(priorityHighButton, priorityRegularButton, priorityLowButton)

        with(selectedElements) {
            if (this.isNotEmpty()) {
                (arrayOf<AbstractButton>(
                    rotateRightButton,
                    rotateLeftButton,
                    deleteButton
                ) + priorityRadioButtons).forEach { it.isEnabled = true }

                visibilityButton.initialize(false)
                nameLabel.text =
                    if (this.size == 1) this[0].name else "$size ${Strings[STR_SELECTED_ELEMENTS, StringStates.NORMAL]}"

                sizeCombo.selectedItem = this

                if (this.size == 1) {
                    lifeSlide.element = this[0]
                    manaSlide.element = this[0]

                    when (this[0].priority) {
                        Priority.HIGH -> priorityHighButton
                        Priority.REGULAR -> priorityRegularButton
                        Priority.LOW -> priorityLowButton
                    }.isSelected = true
                }
            } else {
                nameLabel.text = null
                (arrayOf<AbstractButton>(
                    rotateRightButton,
                    rotateLeftButton,
                    deleteButton
                ) + priorityRadioButtons).forEach { it.isEnabled = false }
                priorityRadioButtons.forEach { it.isSelected = false }
                priorityInvisibleButton.isSelected = true
                visibilityButton.initialize(true)
                sizeCombo.selectedItem = null

                lifeSlide.element = null
                manaSlide.element = null
            }
        }

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
            g.fillRect(15, 15, 110, 110)

            if (this.size == 1) {
                g.drawImage(this[0].sprite.image, 20, 20, 100, 100, null)
            } else {
                g.color = Color.WHITE
                g.fillRect(20, 20, 100, 100)
            }
        }

    }
}
