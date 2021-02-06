package jdr.exia.view.frames.home

import jdr.exia.OLEBO_VERSION
import jdr.exia.localization.STR_VERSION
import jdr.exia.localization.Strings
import jdr.exia.view.frames.home.panels.ActsPanel
import jdr.exia.view.frames.home.panels.HomePanel
import jdr.exia.view.frames.rpg.MasterFrame
import jdr.exia.view.utils.components.FileMenu
import jdr.exia.view.utils.components.templates.JFrameTemplate
import jdr.exia.viewModel.HomeManager
import jdr.exia.viewModel.observer.Action
import jdr.exia.viewModel.observer.Observable
import javax.swing.JMenuBar

/**
 * Main frame of the application. It allows us to create, delete and update an act and an element.
 *
 * This frame will send the selected act to the Games Views
 */
class HomeFrame : JFrameTemplate("Olebo - ${Strings[STR_VERSION]} $OLEBO_VERSION") {
    val manager = HomeManager()

    override val observable: Observable = manager

    var contentPane: HomePanel
        get() = this.getContentPane() as HomePanel
        set(value) = this.setContentPane(value)


    init {
        manager.observer = this

        // This line may cause some issues with database writing ! But without it the X button won't close the program
        this.defaultCloseOperation = DISPOSE_ON_CLOSE

        this.jMenuBar = JMenuBar().apply {
            this.add(FileMenu())
        }

        this.contentPane = ActsPanel(manager)
    }

    override fun update(data: Action) {
        when (data) {
            is Action.Dispose -> this.dispose()
            is Action.Reload -> this.contentPane.reload()
            is Action.Switch -> switchPanel(data.panel)
        }
    }

    private fun switchPanel(panel: HomePanel) {
        this.contentPane = panel
        this.revalidate()
        this.pack()
    }

    override fun dispose() {
        MasterFrame.dispose()
        super.dispose()
    }
}