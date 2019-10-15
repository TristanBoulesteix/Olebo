package jdr.exia.view.mainFrame

import jdr.exia.controller.ViewManager
import jdr.exia.model.act.Act
import jdr.exia.model.element.Element

/*ViewManager is View's facade
this is a singleton*/
object ViewFacade {

    init {

    }

    fun addMarker(token: Element) {
        MasterFrame.setMarker(token)
    }

    fun removeMarker() {
        MasterFrame.removeMarker()
    }

    fun clickNDrop(x: Int, y: Int) {
        ViewManager.clickNDrop(x, y)
    }

    fun moveToken(x: Int, y: Int) {
        ViewManager.moveToken(x, y)
    }

    fun setMapBackground(imageName: String) { //Sets the MapPanels backGround

        MasterFrame.setMapBackground(imageName)
        PlayerFrame.setMapBackground(imageName)
        repaintFrames()
    }


    private fun repaintFrames() { //Repaints both frames simultaneously
        MasterFrame.repaint()
        PlayerFrame.repaint()
    }

    private fun initializeAct(act: Act) { /*this method activates the Player and GM frames to initiate/start back an act	*/







        MasterFrame.isVisible = true
        PlayerFrame.isVisible = true


        /*TODO: give master frame and player frame the objects relative to the current act*/

    }


    fun placeTokensOnMaps(tokens: MutableList<Element>) { //places tokens on both maps at corresponding points
        PlayerFrame.updateMap(tokens)
        MasterFrame.updateMap(tokens)
        repaintFrames()
    }
}
