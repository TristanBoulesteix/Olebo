package view.frames.rpg

import model.element.Element
import model.element.Priority
import model.element.Size
import model.utils.callManager
import view.utils.BACKGROUND_COLOR_SELECT_PANEL
import view.utils.DIMENSION_BUTTON_DEFAULT
import view.utils.applyAndAppendTo
import view.utils.components.SlideStats
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
        set(value) {
            field = value

            if (selectedElements.isNotEmpty()) {
                if (selectedElements.size == 1) {
                    sizeCombo.selectedItem = selectedElements[0].size.name
                }
            } else {
                sizeCombo.selectedItem = null
            }
        }

    private val slidePanel: JPanel

    private val lifeSlide = SlideStats(true)

    private val manaSlide = SlideStats(false)

    private val nameLabel = object : JLabel("Nom") {
        init {
            horizontalTextPosition = CENTER
            border = EmptyBorder(20, 0, 0, 0)
        }

        override fun setText(text: String?) {
            if (text == null) {
                this.isEnabled = false
                super.setText("Nom")
            } else {
                this.isEnabled = true
                super.setText(text)
            }
        }
    }

    private val rotateRightButton = JButton("Pivoter vers la droite").apply {
        preferredSize = DIMENSION_BUTTON_DEFAULT
        addActionListener {
            ViewManager.rotateRight()
        }
    }

    private val rotateLeftButton = JButton("Pivoter vers la gauche").apply {
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
        private val defaultText = "Visibilité"

        private val conditionVisibility
            get() = selectedElements.filter { it.isVisible }.size >= selectedElements.size / 2

        private val contentText
            get() = when {
                selectedElements.isEmpty() -> "Masquer"
                selectedElements.size == 1 -> if (selectedElements[0].isVisible) "Masquer" else "Afficher"
                conditionVisibility -> "Masquer"
                else -> "Afficher"
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

    private val deleteButton = JButton("Supprimer").apply { //Deletes selected Token
        preferredSize = DIMENSION_BUTTON_DEFAULT
        addActionListener {
            selectedElements.forEach(ViewManager::removeToken)
            ViewManager.repaint()
        }
    }

    private val sizeCombo = object : JComboBox<String>(arrayOf("XS", "S", "M", "L", "XL", "XXL")) {
        init {
            addActionListener { _ ->
                selectedElements.forEach {
                    if (selectedItem != it.size) {
                        val newSize = when (this.selectedItem) {
                            "XS" -> Size.XS
                            "S" -> Size.S
                            "M" -> Size.M
                            "L" -> Size.L
                            "XL" -> Size.XL
                            "XXL" -> Size.XXL
                            else -> it.size
                        }
                        ViewManager.activeScene.callManager(newSize, it::cmdDimension)
                    }
                }
                ViewManager.repaint()
            }
            border = EmptyBorder(0, 0, 0, 0)
        }

        override fun setSelectedItem(element: Any?) {
            if (element == null) {
                this.isEnabled = false
                super.setSelectedItem("S")
            } else {
                this.isEnabled = true
                super.setSelectedItem(element)
            }
        }
    }

    init {
        this.layout = GridBagLayout()
        this.preferredSize = Dimension(500, 10)

        val inset = Insets(5, 5, 5, 5)

        this.add(nameLabel, GridBagConstraints().apply {
            this.gridx = 0
            this.gridy = 0
            this.weightx = 1.0
            this.insets = Insets(10, 150, 10, 10)
            this.anchor = GridBagConstraints.LINE_START
        })

        this.add(sizeCombo, GridBagConstraints().apply {
            this.gridx = 0
            this.gridy = 2
            this.weightx = 1.0
            this.insets = Insets(10, 150, 10, 10)
            this.anchor = GridBagConstraints.LINE_START
        })

        this.add(rotateRightButton, GridBagConstraints().apply {
            this.gridx = 1
            this.gridy = 0
            this.weightx = 0.5
            this.insets = inset
            this.anchor = GridBagConstraints.LINE_START
        })

        this.add(rotateLeftButton, GridBagConstraints().apply {
            this.gridx = 1
            this.gridy = 2
            this.weightx = 0.5
            this.insets = inset
            this.anchor = GridBagConstraints.LINE_START
        })

        ButtonGroup().apply {
            priorityHighButton = JRadioButton("Premier plan").apply {
                this.addActionListener {
                    ViewManager.updatePriorityToken(Priority.HIGH)
                }
            }
            priorityRegularButton = JRadioButton("Défaut").apply {
                this.addActionListener {
                    ViewManager.updatePriorityToken(Priority.REGULAR)
                }
            }
            priorityLowButton = JRadioButton("Arrière plan").apply {
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

        this.add(priorityHighButton, GridBagConstraints().apply {
            this.gridx = 2
            this.gridy = 0
            this.weightx = 0.5
            this.weighty = 1.0
            this.insets = inset
            this.anchor = GridBagConstraints.LINE_START
        })

        this.add(priorityRegularButton, GridBagConstraints().apply {
            this.gridx = 2
            this.gridy = 1
            this.weightx = 0.5
            this.weighty = 1.0
            this.insets = inset
            this.anchor = GridBagConstraints.LINE_START
        })

        this.add(priorityLowButton, GridBagConstraints().apply {
            this.gridx = 2
            this.gridy = 2
            this.weightx = 0.5
            this.weighty = 1.0
            this.insets = inset
            this.anchor = GridBagConstraints.LINE_START
        })

        this.add(visibilityButton, GridBagConstraints().apply {
            this.gridx = 3
            this.gridy = 0
            this.weightx = 0.5
            this.insets = inset
            this.anchor = GridBagConstraints.LINE_START
        })

        this.add(deleteButton, GridBagConstraints().apply {
            this.gridx = 3
            this.gridy = 2
            this.weightx = 0.5
            this.insets = Insets(10, 10, 10, 10)
            this.anchor = GridBagConstraints.LINE_START
        })

        slidePanel = JPanel().applyAndAppendTo(this, GridBagConstraints().apply {
            this.gridx = 4
            this.gridy = 0
            this.gridheight = 3
            this.weightx = 2.0
            this.insets = Insets(10, 10, 10, 10)
            this.anchor = GridBagConstraints.FIRST_LINE_START
            this.fill = GridBagConstraints.BOTH
        }) {
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
                (arrayOf<AbstractButton>(rotateRightButton, rotateLeftButton, deleteButton) + priorityRadioButtons).forEach { it.isEnabled = true }

                visibilityButton.initialize(false)
                nameLabel.text = if (this.size == 1) this[0].name else "$size éléments sélectionnés"

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
                (arrayOf<AbstractButton>(rotateRightButton, rotateLeftButton, deleteButton) + priorityRadioButtons).forEach { it.isEnabled = false }
                priorityRadioButtons.forEach { it.isSelected = false }
                priorityInvisibleButton.isSelected = true
                visibilityButton.initialize(true)

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
