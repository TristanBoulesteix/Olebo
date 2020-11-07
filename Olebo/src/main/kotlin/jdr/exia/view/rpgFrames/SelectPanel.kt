package jdr.exia.view.rpgFrames

import jdr.exia.model.element.Element
import jdr.exia.model.element.Size
import jdr.exia.view.utils.BACKGROUND_COLOR_SELECT_PANEL
import jdr.exia.view.utils.applyAndAppendTo
import jdr.exia.view.utils.components.SlideStats
import jdr.exia.viewModel.ViewManager
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
    var selectedElement: Element? = null
        set(value) {
            field = value

            if (selectedElement != null) {
                sizeCombo.selectedItem = selectedElement!!.size.name
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

        init {
            text = defaultText
            preferredSize = Dimension(150, 40)
            addActionListener {
                selectedElement?.let {
                    ViewManager.toggleVisibility(it)
                    this.text = if (it.isVisible) "Masquer" else "Afficher"
                }
            }
        }

        fun initialize(turnOff: Boolean) {
            if (turnOff) {
                text = defaultText
                isEnabled = false
            } else {
                text = if (selectedElement?.isVisible == true) "Masquer" else "Afficher"
                isEnabled = true
            }
        }
    }

    private val deleteButton = JButton("Supprimer").apply { //Deletes selected Token
        preferredSize = Dimension(150, 40)
        addActionListener {
            selectedElement?.let {
                ViewManager.removeToken(it)
                ViewManager.repaint()
            }
        }
    }

    private val sizeCombo = object : JComboBox<String>(arrayOf("XS", "S", "M", "L", "XL", "XXL")) {
        init {
            addActionListener {
                selectedElement?.let {
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

        with(selectedElement) {
            g.color = if (this == null || this.isVisible) {
                Color.BLACK
            } else {
                Color.BLUE
            }
            g.fillRect(15, 15, 110, 110)

            if (this != null) {
                deleteButton.isEnabled = true
                visibilityButton.initialize(false)
                nameLabel.text = this.name

                g.drawImage(this.sprite.image, 20, 20, 100, 100, null)

                lifeSlide.element = this
                manaSlide.element = this
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
