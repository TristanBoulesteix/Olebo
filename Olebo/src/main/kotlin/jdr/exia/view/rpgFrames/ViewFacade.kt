package jdr.exia.view.rpgFrames

import jdr.exia.model.element.Element
import jdr.exia.viewModel.ViewManager

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

    fun selectToken(x: Int, y: Int) {
        ViewManager.selectElement(x, y)
    }

    fun setSelectedToken(token: Element?) {
        SelectPanel.selectedElement = token
        MasterFrame.mapPanel.selectedElement = token
    }

    fun setMapBackground(imageName: String) { //Sets the MapPanels backGround
        MasterFrame.setMapBackground(imageName)
        PlayerFrame.mapBackground = imageName
        MasterMenuBar.initialize()
        repaintFrames()
    }

    fun repaintFrames() { //Repaints both frames simultaneously
        MasterFrame.repaint()
        PlayerFrame.repaint()
    }


    fun turnVisible() { /*this method activates the Player and GM frames to initiate/start back an act	*/
        MasterFrame.isVisible = true


        /*TODO: give master frame and player frame the objects relative to the current act*/
    }


    fun placeTokensOnMaps(tokens: MutableList<Element>) { //places tokens on both maps at corresponding points
        PlayerFrame.map = tokens
        MasterFrame.updateMap(tokens)
    }

    fun unSelectElement() {
        MasterFrame.mapPanel.selectedElement = null
        SelectPanel.selectedElement = null
    }

    fun loadItems() {
        MasterFrame.itemPanel.reloadContent()
    }
}
