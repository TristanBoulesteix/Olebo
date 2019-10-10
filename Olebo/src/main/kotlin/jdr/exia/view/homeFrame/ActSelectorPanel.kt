package jdr.exia.view.homeFrame

import jdr.exia.controller.HomeFrameController
import jdr.exia.model.dao.DAO
import jdr.exia.model.utils.getIcon
import jdr.exia.view.template.components.SelectorPanel
import jdr.exia.view.template.event.ClickListener
import java.awt.*
import java.awt.BorderLayout.CENTER
import java.awt.event.MouseEvent
import javax.swing.*
import javax.swing.border.EmptyBorder

class ActSelectorPanel : SelectorPanel() {
    init {
        this.background = Color(158, 195, 255)
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

    private class ActPanel(private val id: Int, name: String) : JPanel(), ClickListener {
        companion object {
            val DIMENSION_LABEL = Dimension(65, 65)
        }

        init {
            this.maximumSize = Dimension(Int.MAX_VALUE, 65)
            this.border = BorderFactory.createMatteBorder(0, 0, 2, 0, Color.BLACK)
            this.layout = BoxLayout(this, BoxLayout.X_AXIS)

            this.add(JPanel().apply {
                this.layout = GridBagLayout()
                this.add(JLabel(name).apply {
                    this.font = Font("Tahoma", Font.BOLD, 18)
                    this.border = BorderFactory.createEmptyBorder(0, 10, 0, 0)
                }, GridBagConstraints().apply {
                    this.anchor = GridBagConstraints.WEST
                    this.weightx = 1.0
                })
                this.addMouseListener(this@ActPanel)
            })

            this.add(SquareLabel(getIcon("edit_icon", HomeFrameController.javaClass), HomeFrameController::deleteAct))

            this.add(SquareLabel(getIcon("delete_icon", HomeFrameController.javaClass), HomeFrameController::deleteAct))
        }

        override fun mouseClicked(e: MouseEvent?) {
            if (e!!.clickCount == 2) HomeFrameController.launchAct(id)
        }

        private inner class SquareLabel(icon: ImageIcon, private val action: (Int) -> Unit) : JLabel(icon, CENTER), ClickListener {
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
}