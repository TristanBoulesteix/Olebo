package view.frames.rpg

import model.element.Element
import viewModel.ViewManager

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

    fun moveToken(x: Int, y: Int) {
        ViewManager.moveToken(x, y)
    }

    fun setSelectedToken(vararg tokens: Element) {
        SelectPanel.selectedElements = tokens.toList()
        MasterFrame.mapPanel.selectedElements = tokens.toMutableList()
    }

    fun setSelectedToken(token: Element?) = if (token == null) setSelectedToken() else setSelectedToken(token)

    fun setMapBackground(imageName: String) { //Sets the MapPanels backGround
        MasterFrame.setMapBackground(imageName)
        PlayerFrame.mapBackground = imageName
        MasterMenuBar.initialize()
        reloadFrames()
    }

    fun reloadFrames() { //Repaints both frames simultaneously
        MasterMenuBar.reloadCommandItemLabel()
        MasterFrame.reload()
        PlayerFrame.repaint()
    }

    fun turnVisible() { /*this method activates the Player and GM frames to initiate/start back an act	*/
        MasterFrame.isVisible = true


        /*TODO: give master frame and player frame the objects relative to the current act*/
    }

    fun placeTokensOnMaps(tokens: List<Element>) { //places tokens on both maps at corresponding points
        PlayerFrame.map = tokens
        MasterFrame.updateMap(tokens)
    }

    fun unSelectElements() {
        MasterFrame.mapPanel.selectedElements = mutableListOf()
        SelectPanel.selectedElements = mutableListOf()
    }

    fun loadItems() {
        MasterFrame.itemPanel.reloadContent()
    }
}
