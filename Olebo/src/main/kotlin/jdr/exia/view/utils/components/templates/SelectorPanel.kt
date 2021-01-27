package jdr.exia.view.utils.components.templates

import jdr.exia.localization.STR_NO_ELEMENT
import jdr.exia.localization.Strings
import jdr.exia.utils.forElse
import jdr.exia.view.utils.BACKGROUND_COLOR_LIGHT_BLUE
import jdr.exia.view.utils.event.addClickListener
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Font
import java.awt.GridBagLayout
import javax.swing.*
import javax.swing.border.EmptyBorder

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

            pairs.toList().forElse {
                this.add(builder(it.first.toInt(), it.second))
            } ?: this.add(JPanel().apply {
                this.layout = GridBagLayout()
                this.add(JLabel(Strings[STR_NO_ELEMENT]).apply {
                    this.font = Font("Tahoma", Font.BOLD, 20)
                })
            })

            this.addClickListener { requestFocusInWindow() }
        }

        (layout as BorderLayout).getLayoutComponent(BorderLayout.CENTER)?.let {
            this.remove(it)
        }
        this.add(JScrollPane(panel), BorderLayout.CENTER)
    }

    /**
     * Refresh the panel with new datas
     */
    open fun refresh() {
        this.createJPanelWithItemSelectablePanel()
        this.revalidate()
        this.repaint()
    }
}