package jdr.exia.view.frames.home

import jdr.exia.OLEBO_VERSION
import jdr.exia.localization.STR_VERSION
import jdr.exia.localization.Strings
import jdr.exia.view.frames.Reloadable
import jdr.exia.view.frames.rpg.MasterFrame
import jdr.exia.view.utils.components.FileMenu
import jdr.exia.view.utils.components.templates.JFrameTemplate
import jdr.exia.viewModel.HomeManager
import jdr.exia.viewModel.pattern.observer.Action
import jdr.exia.viewModel.pattern.observer.Observable
import javax.swing.JMenuBar

/**
 * Main frame of the application. It allows us to create, delete and update an act and an element.
 *
 * This frame will send the selected act to the Games Views
 */
class HomeFrame : JFrameTemplate("Olebo - ${Strings[STR_VERSION]} $OLEBO_VERSION") {
    override val observable: Observable = HomeManager

    init {
        HomeManager.observer = this

        // This line may cause some issues with database writing ! But without it the X button won't close the program
        this.defaultCloseOperation = DISPOSE_ON_CLOSE

        this.jMenuBar = JMenuBar().apply {
            this.add(FileMenu())
        }

        this.contentPane = HomePanel()
    }

    override fun update(data: Action) {
        when (data) {
            Action.DISPOSE -> this.dispose()
            Action.REFRESH -> (this.contentPane as? Reloadable)?.reload()
        }
    }

    override fun dispose() {
        MasterFrame.dispose()
        super.dispose()
    }
}