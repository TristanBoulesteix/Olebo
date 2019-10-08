package jdr.exia.view.homeFrame

import jdr.exia.model.dao.DAO
import java.awt.*
import java.awt.BorderLayout.CENTER
import javax.swing.*
import javax.swing.border.EmptyBorder

class ActSelectorPanel : JPanel() {
    init {
        this.background = Color.BLUE
        this.border = EmptyBorder(20, 20, 20, 20)
        this.layout = BorderLayout()

        val listPanel = JPanel().apply {
            this.layout = BoxLayout(this, BoxLayout.Y_AXIS)

            DAO.getActsList().forEach {
                this.add(ActPanel(it.first.toInt(), it.second))
            }
        }

        this.add(JScrollPane(listPanel), CENTER)
    }

    private class ActPanel(val id: Int, name: String) : JPanel() {
        init {
            this.maximumSize = Dimension(Int.MAX_VALUE, 100)
            this.border = BorderFactory.createMatteBorder(0, 0, 2, 0, Color.BLACK)
            this.layout = BoxLayout(this, BoxLayout.X_AXIS)

            this.add(JPanel().apply {
                this.layout = GridBagLayout()
                this.add(JLabel(name).apply {
                    this.font = Font("Tahoma", Font.BOLD, 18)
                    this.border = BorderFactory.createEmptyBorder(0,10,0,0)
                }, GridBagConstraints().apply {
                    this.anchor = GridBagConstraints.WEST
                    this.weightx = 1.0
                })
            })
        }
    }
}