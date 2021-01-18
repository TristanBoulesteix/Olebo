package jdr.exia.view.frames.rpg

import jdr.exia.localization.*
import jdr.exia.model.dao.option.Settings
import jdr.exia.model.element.Element
import jdr.exia.model.element.Priority
import jdr.exia.model.element.Size
import jdr.exia.model.utils.Elements
import jdr.exia.model.utils.callManager
import jdr.exia.model.utils.emptyElements
import jdr.exia.view.utils.BACKGROUND_COLOR_SELECT_PANEL
import jdr.exia.view.utils.DIMENSION_BUTTON_DEFAULT
import jdr.exia.view.utils.components.templates.ComboSelectPanel
import jdr.exia.view.utils.components.templates.StatsLabel
import jdr.exia.view.utils.gridBagConstraintsOf
import jdr.exia.viewModel.ViewManager
import java.awt.*
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField


/**
 * Contains all this info regarding the item selected by the Game Master.
 *
 * This is a singleton.
 */
object SelectPanel : JPanel() {
    private val defaultLeftInsets = Insets(10, 150, 10, 10)

    private val leftPanel = object : JPanel() {
        init {
            this.layout = GridBagLayout()
            this.isOpaque = false
        }
    }

    private val blueprintNameLabel = object : JLabel() {
        init {
            horizontalTextPosition = LEFT
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

    private val nameLabel = object : JTextField(Strings[STR_LABEL]) {
        init {
            this.isEnabled = false
        }

        override fun setEnabled(enabled: Boolean) {
            if (!enabled)
                this.text = Strings[STR_LABEL]
            super.setEnabled(enabled)
        }
    }

    private var sizeCombo = SizeCombo()
        set(combo) {
            leftPanel.remove(field)
            leftPanel.add(
                combo,
                gridBagConstraintsOf(
                    0,
                    if (Settings.isLabelEnabled) 3 else 2,
                    weightx = 1.0,
                    insets = defaultLeftInsets,
                    anchor = GridBagConstraints.LINE_START,
                    fill = GridBagConstraints.HORIZONTAL
                )
            )
            field = combo
        }

    private var priorityCombo = PriorityCombo()
        set(combo) {
            leftPanel.remove(field)
            leftPanel.add(
                combo,
                gridBagConstraintsOf(
                    0,
                    if (Settings.isLabelEnabled) 4 else 3,
                    weightx = 1.0,
                    insets = defaultLeftInsets,
                    anchor = GridBagConstraints.LINE_START,
                    fill = GridBagConstraints.HORIZONTAL
                )
            )
            field = combo
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
            leftPanel, gridBagConstraintsOf(
                0,
                0,
                weightx = .5,
                insets = defaultLeftInsets,
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
            initilializeComboInOrder(this)
            nameLabel.isEnabled = false

            if (this.isNotEmpty()) {
                arrayOf(rotateRightButton, rotateLeftButton, deleteButton).forEach { it.isEnabled = true }

                visibilityButton.initialize(false)
                blueprintNameLabel.text =
                    if (this.size == 1) this[0].name else "$size ${Strings[STR_SELECTED_ELEMENTS, StringStates.NORMAL]}"

                if (this.size == 1) {
                    lifeField.element = this[0]
                    manaField.element = this[0]
                    nameLabel.let {
                        it.text = this[0].name
                        it.isEnabled = true
                    }
                }
            } else {
                blueprintNameLabel.text = null

                arrayOf(rotateRightButton, rotateLeftButton, deleteButton).forEach {
                    it.isEnabled = false
                }

                visibilityButton.initialize(true)


                sizeCombo = SizeCombo()

                lifeField.element = null
                manaField.element = null
            }
        }

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

    /**
     * This function initialize the [ComboSelectPanel] in the right order. It is preferable to use this function instead of the constructor of the [ComboSelectPanel].
     */
    private fun initilializeComboInOrder(element: Elements?) {
        leftPanel.removeAll()
        leftPanel.add(
            blueprintNameLabel, gridBagConstraintsOf(
                0,
                0,
                weightx = 1.0,
                insets = defaultLeftInsets,
                anchor = GridBagConstraints.LINE_START,
                fill = GridBagConstraints.HORIZONTAL
            )
        )

        if (Settings.isLabelEnabled)
            leftPanel.add(
                nameLabel, gridBagConstraintsOf(
                    0,
                    1,
                    weightx = 1.0,
                    insets = defaultLeftInsets,
                    anchor = GridBagConstraints.LINE_START,
                    fill = GridBagConstraints.HORIZONTAL
                )
            )

        priorityCombo = PriorityCombo(element)
        sizeCombo = SizeCombo(element)
    }

    private class SizeCombo(items: Elements? = null) :
        ComboSelectPanel(arrayOf("XS", "S", "M", "L", "XL", "XXL"), items) {
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
        }

        override fun setSelectedItem(selected: Any?) {
            this.toSelect = selected.doIfElement("S") {
                it.size.name
            }
        }
    }

    private class PriorityCombo(items: Elements? = null) :
        ComboSelectPanel(arrayOf(Strings[STR_FOREGROUND], Strings[STR_DEFAULT], Strings[STR_DEFAULT]), items) {
        init {
            addActionListener {
                val newPriority = when (selectedItem) {
                    Strings[STR_BACKGROUND] -> Priority.LOW
                    Strings[STR_FOREGROUND] -> Priority.HIGH
                    else -> Priority.REGULAR
                }

                ViewManager.updatePriorityToken(newPriority)
            }
        }

        override fun setSelectedItem(selected: Any?) {
            this.toSelect = selected.doIfElement(Strings[STR_DEFAULT]) {
                when (it.priority) {
                    Priority.HIGH -> Strings[STR_FOREGROUND]
                    Priority.LOW -> Strings[STR_BACKGROUND]
                    Priority.REGULAR -> Strings[STR_DEFAULT]
                }
            }
        }
    }
}