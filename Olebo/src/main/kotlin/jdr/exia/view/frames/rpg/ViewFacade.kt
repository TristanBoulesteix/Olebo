package jdr.exia.view.frames.rpg

import jdr.exia.model.dao.option.Settings
import jdr.exia.model.element.Element
import jdr.exia.model.utils.Elements
import jdr.exia.model.utils.emptyElements
import jdr.exia.model.utils.toJColor

/**
 * ViewManager is View's facade
 * This is a singleton
 */
object ViewFacade {
    var actName: String = ""
        set(value) {
            MasterFrame.title = value
            PlayerFrame.title = value
            field = value
        }

    fun setSelectedToken(vararg tokens: Element) {
        MasterFrame.selectedElements = tokens.toMutableList()
    }

    fun setSelectedToken(token: Element?) = if (token == null) setSelectedToken() else setSelectedToken(token)

    fun setMapBackground(imageName: String) { //Sets the MapPanels backGround
        MasterFrame.setMapBackground(imageName)
        PlayerFrame.mapBackground = imageName
        MasterMenuBar.initialize()
        reloadFrames()
    }

    /**
     * Repaints both frames simultaneously
     */
    fun reloadFrames() {
        MasterMenuBar.reloadCommandItemLabel()
        MasterFrame.reload()
        PlayerFrame.reload()
    }

    fun updateCursorOnPlayerFrame() {
        Settings.cursorColor.let {
            PlayerFrame.updateCursor(it.contentCursorColor.toJColor(), it.borderCursorColor.toJColor())
        }
    }

    /**
     * This method activates the [PlayerFrame] and [MasterFrame] to initiate/start back an Act]
     */
    fun turnVisible() {
        MasterFrame.isVisible = true
    }

    fun placeTokensOnMaps(tokens: Elements) { //places tokens on both maps at corresponding points
        PlayerFrame.map = tokens
        MasterFrame.updateMap(tokens)
    }

    fun unSelectElements() {
        MasterFrame.selectedElements = emptyElements()
    }

    fun loadItems() {
        MasterFrame.itemPanel.reload()
    }
}
