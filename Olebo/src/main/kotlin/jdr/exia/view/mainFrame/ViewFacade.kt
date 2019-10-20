package jdr.exia.view.mainFrame

import jdr.exia.controller.ViewManager
import jdr.exia.model.element.Element

/*ViewManager is View's facade
this is a singleton*/
object ViewFacade {

    fun testRun(){ //TODO: remove once test is not needed
        MasterFrame.isVisible = true
        PlayerFrame.isVisible = true
    }

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

    fun selectToken(x: Int,y:Int){

        ViewManager.selectToken(x,y)

    }

    fun setSelectedToken(token: Element){
        SelectPanel.selectedElement = token
    }

    fun setMapBackground(imageName: String) { //Sets the MapPanels backGround
        MasterFrame.setMapBackground(imageName)
        PlayerFrame.setMapBackground(imageName)
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
        PlayerFrame.updateMap(tokens)
        MasterFrame.updateMap(tokens)
        repaintFrames()

    }


}
