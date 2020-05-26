package jdr.exia.view.rpg.frames

import jdr.exia.view.rpg.frames.utility.panel.ItemPanel
import jdr.exia.view.utils.update
import java.awt.Font
import javax.swing.JTabbedPane


class ToolsPanel: JTabbedPane() {
    val itemPanel = ItemPanel()

    init {
        this.font = this.font.update(size = 12, style = Font.BOLD)
        this.isOpaque = true

        this.addTab("Items", itemPanel)
    }
}