package jdr.exia.view.utils.components.templates

import jdr.exia.view.utils.components.filter.IntegerFilter
import jdr.exia.view.utils.applyAndAppendTo
import jdr.exia.view.utils.event.ClickListener
import jdr.exia.view.utils.event.addFocusLostListener
import jdr.exia.view.utils.gridBagConstraintsOf
import java.awt.*
import java.io.File
import javax.imageio.ImageIO
import javax.swing.*
import javax.swing.text.PlainDocument

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

        private val listener = ClickListener { action(id) }

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

            JTextField(text).applyAndAppendTo(
                this,
                gridBagConstraintsOf(fill = GridBagConstraints.BOTH, weightx = 1.0, weighty = 1.0)
            ) {
                this.isOpaque = false
                this.isEnabled = isEditable
                (this.document as PlainDocument).documentFilter = IntegerFilter()
                this.font = Font("Tahoma", Font.BOLD, 18)
                this.horizontalAlignment = JTextField.CENTER
                this.addFocusLostListener {
                    if (!it.isTemporary) {
                        if (action != null) {
                            action(id, this@applyAndAppendTo.text)
                        }
                    }
                }
            }
        }
    }
}