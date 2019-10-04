package jdr.exia.view.acts

import java.awt.BorderLayout
import java.awt.Color
import java.awt.Dimension
import java.awt.Font
import javax.swing.JFrame
import javax.swing.JList
import javax.swing.JScrollPane
import javax.swing.border.EmptyBorder

class ActSelector(acts: Array<String>) : JFrame("Sélection de l'acte") {
    private val defaultDimension = Dimension(800, 800)

    init {
        // Init the JFrame settings
        this.minimumSize = defaultDimension
        this.size = defaultDimension
        this.setLocationRelativeTo(null)
        this.isResizable = true
        this.defaultCloseOperation = EXIT_ON_CLOSE
        this.layout = BorderLayout()

        // Init components
        this.add(JScrollPane(JList<String>(acts).apply {
            border = EmptyBorder(10,10,10,10)
            font = Font("Arial", Font.BOLD, 16)
            selectionBackground = Color.ORANGE
        }), BorderLayout.SOUTH)

        this.pack()
    }
}