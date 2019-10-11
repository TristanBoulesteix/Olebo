package jdr.exia.view.template.components

import jdr.exia.pattern.Observable
import jdr.exia.pattern.Observer
import jdr.exia.view.template.event.ClickListener
import java.awt.*
import java.awt.event.MouseEvent
import javax.swing.*
import javax.swing.border.Border
import javax.swing.border.EmptyBorder

abstract class JFrameTemplate(title: String) : JFrame(title), Observer {
    protected companion object {
        val DIMENSION_FRAME = Dimension(600, 800)
        val BORDER_BUTTONS: Border = BorderFactory.createEmptyBorder(15, 15, 15, 15)
    }

    protected abstract val observable : Observable

    init {
        this.minimumSize = DIMENSION_FRAME
        this.preferredSize = DIMENSION_FRAME
        this.defaultCloseOperation = EXIT_ON_CLOSE
        this.setLocationRelativeTo(null)
        this.layout = BorderLayout()
    }

    override fun dispose() {
        observable.observer = null
        super.dispose()
    }
}

abstract class SelectorPanel : JPanel() {
    protected abstract val pairs: Array<Pair<String, String>>

    protected abstract fun builder(id: Int, name: String) : ItemSelectablePanel

    init {
        this.background = Color(158, 195, 255)
        this.border = EmptyBorder(20,20,20,20)
        this.layout = BorderLayout()

        this.createJPanelWithItemSelectablePanel()
    }

    private fun createJPanelWithItemSelectablePanel() {
        val panel = JPanel().apply {
            this.layout = BoxLayout(this, BoxLayout.Y_AXIS)

            pairs.forEach {
                this.add(builder(it.first.toInt(), it.second))
            }
        }

        this.add(JScrollPane(panel), BorderLayout.CENTER)
        this.revalidate()
    }

    fun refresh() {
        this.createJPanelWithItemSelectablePanel()
    }
}

@Suppress("LeakingThis")
abstract class ItemSelectablePanel(protected val id: Int, name: String) : JPanel() {
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

    protected inner class SquareLabel(icon: ImageIcon, private val action: (Int) -> Unit) : JLabel(icon, CENTER),
        ClickListener {
        init {
            this.preferredSize = DIMENSION_LABEL
            this.maximumSize = DIMENSION_LABEL
            this.border = BorderFactory.createLineBorder(Color.YELLOW)
            this.addMouseListener(this)
        }

        override fun mouseClicked(e: MouseEvent?) {
            action(id)
        }
    }
}