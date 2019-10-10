package jdr.exia.view.template.components

import java.awt.BorderLayout
import java.awt.Color
import javax.swing.JPanel
import javax.swing.border.EmptyBorder

abstract class SelectorPanel : JPanel() {
    init {
        this.background = Color(158, 195, 255)
        this.border = EmptyBorder(20, 20, 20, 20)
        this.layout = BorderLayout()

/*        val listPanel = JPanel().apply {
            this.layout = BoxLayout(this, BoxLayout.Y_AXIS)

            DAO.getActsList().forEach {
                this.add(ActSelectorPanel.ActPanel(it.first.toInt(), it.second))
            }
        }

        this.add(JScrollPane(listPanel), BorderLayout.CENTER)*/
    }
}