package jdr.exia.view.utils.components

import jdr.exia.pattern.observer.Observable
import jdr.exia.pattern.observer.Observer
import jdr.exia.view.utils.BACKGROUND_COLOR_LIGHT_BLUE
import jdr.exia.view.utils.DIMENSION_FRAME
import jdr.exia.view.utils.event.ClickListener
import java.awt.*
import java.awt.event.MouseEvent
import javax.swing.*
import javax.swing.border.EmptyBorder

/**
 * Template of all JFrame's menu templates
 */
abstract class JFrameTemplate(title: String) : JFrame(),
    Observer {
    protected abstract val observable: Observable

    init {
        this.title = title
        this.minimumSize = DIMENSION_FRAME
        this.preferredSize = DIMENSION_FRAME
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
        this.minimumSize = DIMENSION_FRAME
        this.preferredSize = DIMENSION_FRAME
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

            pairs.forElse {
                this.add(builder(it.first.toInt(), it.second))
            } ?: this.add(JPanel().apply {
                this.layout = GridBagLayout()
                this.add(JLabel("Aucun élément").apply {
                    this.font = Font("Tahoma", Font.BOLD, 20)
                })
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
    fun refresh() {
        this.createJPanelWithItemSelectablePanel()
        this.revalidate()
        this.repaint()
    }

    private fun <T> Array<T>.forElse(block: (T) -> Unit) = if (isEmpty()) null else forEach(block)
}

/**
 * Template for panel which display an item.
 */
@Suppress("LeakingThis")
abstract class ItemPanel(protected val id: Int, name: String) : JPanel() {
    companion object {
        val DIMENSION_LABEL = Dimension(65, 65)
    }

    protected val namePanel = JPanel().apply {
        this.layout = GridBagLayout()
        this.add(JLabel(name).apply {
            this.font = Font("Tahoma", Font.BOLD, 18)
            this.border = BorderFactory.createEmptyBorder(0, 10, 0, 0)
        }, GridBagConstraints().apply {
            this.anchor = GridBagConstraints.WEST
            this.weightx = 1.0
        })
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
    protected inner class SquareLabel(icon: ImageIcon, private val action: (Int) -> Unit) :
        JLabel(icon, CENTER) {
        init {
            this.preferredSize = DIMENSION_LABEL
            this.maximumSize = DIMENSION_LABEL
            this.border = BorderFactory.createMatteBorder(0, 2, 0, 0, Color.BLACK)
            this.addMouseListener(object : ClickListener {
                override fun mouseClicked(e: MouseEvent?) {
                    action(id)
                }
            })
        }
    }
}