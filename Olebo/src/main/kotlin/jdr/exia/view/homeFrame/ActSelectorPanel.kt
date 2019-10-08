package jdr.exia.view.homeFrame

import java.awt.BorderLayout
import java.awt.BorderLayout.CENTER
import java.awt.Color
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.border.EmptyBorder

class ActSelectorPanel : JPanel() {
    init {
        this.background = Color.BLUE
        this.border = EmptyBorder(20,20,20,20)
        this.layout = BorderLayout()

        val listPanel = JPanel().apply {

        }

        this.add(JScrollPane(listPanel), CENTER)
    }

    private inner class ActPanel() : JPanel() {

    }
}