package jdr.exia.view.utils.components.templates

import jdr.exia.localization.STR_NO_ELEMENT
import jdr.exia.localization.StringLocale
import jdr.exia.utils.forElse
import jdr.exia.view.frames.Reloadable
import jdr.exia.view.utils.BACKGROUND_COLOR_LIGHT_BLUE
import jdr.exia.view.utils.event.addClickListener
import jdr.exia.viewModel.ArrayOfPairs
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Font
import java.awt.GridBagLayout
import javax.swing.*
import javax.swing.border.EmptyBorder
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * This panel is a template for all panels which display a list of components
 */
abstract class SelectorPanel(pairBuilder: PairArrayBuilder) : JPanel(), Reloadable {
    private val jScrollPane = JScrollPane()

    private val pairs by pairBuilder

    protected abstract fun builder(id: Int, name: String): ItemPanel

    init {
        this.background = BACKGROUND_COLOR_LIGHT_BLUE
        this.border = EmptyBorder(20, 20, 20, 20)
        this.layout = BorderLayout()

        this.add(jScrollPane, BorderLayout.CENTER)

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
                this.add(JLabel(StringLocale[STR_NO_ELEMENT]).apply {
                    this.font = Font("Tahoma", Font.BOLD, 20)
                })
            })

            this.addClickListener { requestFocusInWindow() }
        }

        jScrollPane.setViewportView(panel)
    }

    /**
     * Refresh the panel with new datas
     */
    override fun reload() {
        this.createJPanelWithItemSelectablePanel()
        this.revalidate()
        this.repaint()
    }

    class PairArrayBuilder(private val pairBuilder: () -> ArrayOfPairs) :
        ReadOnlyProperty<SelectorPanel, ArrayOfPairs> {
        override fun getValue(thisRef: SelectorPanel, property: KProperty<*>) = pairBuilder()
    }
}