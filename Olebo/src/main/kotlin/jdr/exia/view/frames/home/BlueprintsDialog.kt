package jdr.exia.view.frames.home

import jdr.exia.localization.STR_MANAGE_BLUEPRINTS
import jdr.exia.localization.Strings
import jdr.exia.view.frames.home.panels.BlueprintEditorPanel
import jdr.exia.view.utils.components.templates.JDialogTemplate
import jdr.exia.viewModel.HomeManager
import jdr.exia.viewModel.observer.Action
import java.awt.Window

class BlueprintsDialog(parent: Window?) : JDialogTemplate(parent, Strings[STR_MANAGE_BLUEPRINTS], true) {
    val panel : BlueprintEditorPanel

    init {
        HomeManager().apply {
            this.observer = this@BlueprintsDialog
            panel = BlueprintEditorPanel(this, false)
        }

        this.contentPane = panel
    }

    override fun update(data: Action) {
        if (data is Action.Reload) {
            panel.reload()
        }
    }
}