package view.homeFrame

import VERSION
import view.utils.BORDER_BUTTONS
import view.utils.components.FileMenu
import view.utils.components.JFrameTemplate
import viewModel.HomeManager
import viewModel.pattern.observer.Action
import viewModel.pattern.observer.Observable
import java.awt.BorderLayout.CENTER
import java.awt.BorderLayout.NORTH
import java.awt.Color
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.BorderFactory
import javax.swing.JButton
import javax.swing.JMenuBar
import javax.swing.JPanel

/**
 * Main frame of the application. It allows us to create, delete and update an act and an element.
 *
 * This frame will send the selected act to the Games Views
 */
class HomeFrame : JFrameTemplate("Olebo - Version $VERSION") {
    override val observable: Observable = HomeManager

    private val selectorPanel = ActSelectorPanel()

    init {
        HomeManager.observer = this

        // This line may cause some issues with database writing ! But without it the X button won't close the program
        this.defaultCloseOperation = DISPOSE_ON_CLOSE

        this.jMenuBar = JMenuBar().apply {
            this.add(FileMenu())
        }

        this.add(JPanel().apply {
            this.border = BorderFactory.createEmptyBorder(15, 0, 15, 0)
            this.layout = GridBagLayout()

            val elementButton = JButton("Éléments").apply {
                this.border = BORDER_BUTTONS
                this.addActionListener {
                    HomeManager.openObjectEditorFrame()
                }
            }
            val cElementButton = GridBagConstraints().apply {
                this.gridx = 0
                this.gridy = 0
                this.weightx = .5
            }
            this.add(elementButton, cElementButton)

            val actButton = JButton("Ajouter un scénario").apply {
                this.border = BORDER_BUTTONS
                this.addActionListener {
                    HomeManager.openActCreatorFrame()
                }
            }
            val cActButton = GridBagConstraints().apply {
                this.gridx = 1
                this.gridy = 0
                this.weightx = .5
            }
            this.add(actButton, cActButton)

            this.background = Color.ORANGE
        }, NORTH)

        this.add(selectorPanel, CENTER)

        this.pack()
    }

    override fun update(data: Action) {
        when (data) {
            Action.DISPOSE -> this.dispose()
            Action.REFRESH -> this.selectorPanel.refresh()
        }
    }
}