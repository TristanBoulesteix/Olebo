package view.frames.rpg

import model.element.Element
import model.element.Size
import model.utils.callManager
import view.utils.BACKGROUND_COLOR_SELECT_PANEL
import view.utils.DIMENSION_BUTTON_DEFAULT
import view.utils.applyAndAppendTo
import view.utils.components.SlideStats
import viewModel.ViewManager
import java.awt.*
import javax.swing.JButton
import javax.swing.JComboBox
import javax.swing.JLabel
import javax.swing.JPanel
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

    private val priorityUpButton = JButton("Avancer").apply {
        preferredSize = DIMENSION_BUTTON_DEFAULT
        addActionListener {

        }
    }

    private val priorityDownButton = JButton("Reculer").apply {
        preferredSize = DIMENSION_BUTTON_DEFAULT
        addActionListener {

        }
    }

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
                selectedElements.forEach {
                    ViewManager.toggleVisibility(it, visibility)
                }
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

        this.add(nameLabel, GridBagConstraints().apply {
            this.gridx = 0
            this.gridy = 0
            this.weightx = 1.0
            this.insets = Insets(10, 150, 10, 10)
            this.anchor = GridBagConstraints.FIRST_LINE_START
        })

        this.add(sizeCombo, GridBagConstraints().apply {
            this.gridx = 0
            this.gridy = 1
            this.weightx = 1.0
            this.insets = Insets(10, 150, 10, 10)
            this.anchor = GridBagConstraints.FIRST_LINE_START
        })

        this.add(rotateRightButton, GridBagConstraints().apply {
            this.gridx = 1
            this.gridy = 0
            this.weightx = 0.5
            this.insets = Insets(5, 5, 5, 5)
            this.anchor = GridBagConstraints.FIRST_LINE_START
        })

        this.add(rotateLeftButton, GridBagConstraints().apply {
            this.gridx = 1
            this.gridy = 1
            this.weightx = 0.5
            this.insets = Insets(5, 5, 5, 5)
            this.anchor = GridBagConstraints.FIRST_LINE_START
        })

        this.add(priorityUpButton, GridBagConstraints().apply {
            this.gridx = 2
            this.gridy = 0
            this.weightx = 0.5
            this.insets = Insets(5, 5, 5, 5)
            this.anchor = GridBagConstraints.FIRST_LINE_START
        })

        this.add(priorityDownButton, GridBagConstraints().apply {
            this.gridx = 2
            this.gridy = 1
            this.weightx = 0.5
            this.insets = Insets(5, 5, 5, 5)
            this.anchor = GridBagConstraints.FIRST_LINE_START
        })

        this.add(visibilityButton, GridBagConstraints().apply {
            this.gridx = 3
            this.gridy = 0
            this.weightx = 0.5
            this.insets = Insets(5, 5, 5, 5)
            this.anchor = GridBagConstraints.FIRST_LINE_START
        })

        this.add(deleteButton, GridBagConstraints().apply {
            this.gridx = 3
            this.gridy = 1
            this.weightx = 0.5
            this.insets = Insets(5, 5, 5, 5)
            this.anchor = GridBagConstraints.FIRST_LINE_START
        })

        slidePanel = JPanel().applyAndAppendTo(this, GridBagConstraints().apply {
            this.gridx = 4
            this.gridy = 0
            this.gridheight = 2
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
        with(selectedElements) {
            if (this.isNotEmpty()) {
                rotateRightButton.isEnabled = true
                rotateLeftButton.isEnabled = true
                deleteButton.isEnabled = true
                visibilityButton.initialize(false)
                nameLabel.text = if (this.size == 1) this[0].name else "$size éléments sélectionnés"

                if (this.size == 1) {
                    lifeSlide.element = this[0]
                    manaSlide.element = this[0]
                }
            } else {
                nameLabel.text = null
                rotateRightButton.isEnabled = false
                rotateLeftButton.isEnabled = false
                deleteButton.isEnabled = false
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
