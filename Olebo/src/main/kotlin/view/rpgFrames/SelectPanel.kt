package view.rpgFrames

import model.element.Element
import model.element.Size
import view.utils.BACKGROUND_COLOR_SELECT_PANEL
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
                } else {

                }
            } else {
                sizeCombo.selectedItem = null
            }
        }

    private val slidePanel: JPanel

    private var lifeSlide = SlideStats(true)

    private var manaSlide = SlideStats(false)

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
            preferredSize = Dimension(150, 40)
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
        preferredSize = Dimension(150, 40)
        addActionListener {
            selectedElements.forEach(ViewManager::removeToken)
            ViewManager.repaint()
        }
    }

    private val sizeCombo = object : JComboBox<String>(arrayOf("XS", "S", "M", "L", "XL", "XXL")) {
        init {
            addActionListener {
                selectedElements.forEach {
                    if (selectedItem != it.size) {
                        when (this.selectedItem) {
                            "XS" -> it.size = Size.XS
                            "S" -> it.size = Size.S
                            "M" -> it.size = Size.M
                            "L" -> it.size = Size.L
                            "XL" -> it.size = Size.XL
                            "XXL" -> it.size = Size.XXL
                        }
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
            this.weightx = 0.5
            this.insets = Insets(10, 150, 10, 10)
            this.anchor = GridBagConstraints.FIRST_LINE_START
        })

        this.add(sizeCombo, GridBagConstraints().apply {
            this.gridx = 0
            this.gridy = 1
            this.weightx = 0.5
            this.insets = Insets(10, 150, 10, 10)
            this.anchor = GridBagConstraints.FIRST_LINE_START
        })

        this.add(visibilityButton, GridBagConstraints().apply {
            this.gridx = 1
            this.gridy = 0
            this.weightx = 0.0
            this.insets = Insets(10, 10, 10, 10)
            this.anchor = GridBagConstraints.FIRST_LINE_START
        })

        this.add(deleteButton, GridBagConstraints().apply {
            this.gridx = 1
            this.gridy = 1
            this.weightx = 0.0
            this.insets = Insets(10, 10, 10, 10)
            this.anchor = GridBagConstraints.FIRST_LINE_START
        })

        slidePanel = JPanel().applyAndAppendTo(this, GridBagConstraints().apply {
            this.gridx = 2
            this.gridy = 0
            this.gridheight = 2
            this.weightx = 1.0
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

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)

        with(selectedElements) {
            g.color = if (this.isEmpty() || this.all { it.isVisible }) {
                Color.BLACK
            } else {
                Color.BLUE
            }
            g.fillRect(15, 15, 110, 110)

            if (this.isNotEmpty()) {
                deleteButton.isEnabled = true
                visibilityButton.initialize(false)
                nameLabel.text = if (this.size == 1) this[0].name else "$size éléments sélectionnés"

                if (this.size == 1) {
                    g.drawImage(this[0].sprite.image, 20, 20, 100, 100, null)

                    lifeSlide.element = this[0]
                    manaSlide.element = this[0]
                }
            } else {
                nameLabel.text = null
                deleteButton.isEnabled = false
                visibilityButton.initialize(true)

                g.color = Color.WHITE
                g.fillRect(20, 20, 100, 100)

                lifeSlide.element = null
                manaSlide.element = null

            }
        }

        this.revalidate()
    }
}