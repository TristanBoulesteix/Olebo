package jdr.exia.view.frames.rpg

import jdr.exia.model.dao.option.Settings
import jdr.exia.model.element.Element
import jdr.exia.model.element.Elements
import jdr.exia.model.element.emptyElements
import java.io.File
import javax.imageio.ImageIO

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
        MasterFrame.selectedElements = Elements(*tokens)
    }

    fun setSelectedToken(token: Element?) = if (token == null) setSelectedToken() else setSelectedToken(token)

    /**
     * Sets the [MapPanel]s backGround
     */
    fun setMapBackground(imageName: String) {
        val background = ImageIO.read(File(imageName))
        MasterFrame.mapBackground = background
        PlayerFrame.mapBackground = background
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
            PlayerFrame.updateCursor(it.contentColor, it.borderColor)
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
