package jdr.exia.view.frames.home

import jdr.exia.OLEBO_VERSION
import jdr.exia.localization.STR_ADD_ACT
import jdr.exia.localization.STR_ELEMENTS
import jdr.exia.localization.STR_VERSION
import jdr.exia.localization.Strings
import jdr.exia.view.frames.rpg.MasterFrame
import jdr.exia.view.utils.BORDER_BUTTONS
import jdr.exia.view.utils.components.FileMenu
import jdr.exia.view.utils.components.JFrameTemplate
import jdr.exia.view.utils.gridBagConstraintsOf
import jdr.exia.viewModel.HomeManager
import jdr.exia.viewModel.pattern.observer.Action
import jdr.exia.viewModel.pattern.observer.Observable
import java.awt.BorderLayout.CENTER
import java.awt.BorderLayout.NORTH
import java.awt.Color
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
class HomeFrame : JFrameTemplate("Olebo - ${Strings[STR_VERSION]} $OLEBO_VERSION") {
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

            val elementButton = JButton(Strings[STR_ELEMENTS]).apply {
                this.border = BORDER_BUTTONS
                this.addActionListener {
                    HomeManager.openObjectEditorFrame()
                }
            }

            this.add(elementButton, gridBagConstraintsOf(gridx = 0, gridy = 0, weightx = .5))

            val actButton = JButton(Strings[STR_ADD_ACT]).apply {
                this.border = BORDER_BUTTONS
                this.addActionListener {
                    HomeManager.openActCreatorFrame()
                }
            }

            this.add(actButton, gridBagConstraintsOf(gridx = 1, gridy = 0, weightx = .5))

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

    override fun dispose() {
        MasterFrame.dispose()
        super.dispose()
    }
}