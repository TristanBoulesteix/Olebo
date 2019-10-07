package jdr.exia.view

import jdr.exia.model.element.Element
import java.awt.GraphicsDevice
import java.awt.GraphicsEnvironment
import javax.swing.JFrame

/*ViewManager is View's facade
this is a singleton*/
object ViewManager {






    init {
        this.initializeActFrames() //Temporary, needs to be altered later
    }


    fun refreshFrames() {  // Refreshes Player and MasterFrames at once
       // masterFrame.mapPanel.refresh()
    }

    fun setMapBackground(imageName: String){

        MasterFrame.setMapBackground(imageName)
        PlayerFrame.setMapBackground(imageName)
        repaintFrames()
    }

    fun repaintFrames(){
        MasterFrame.repaint()
        PlayerFrame.repaint()
    }

    private fun initializeActFrames()
    { /*this method activates the Player and GM frames to initiate/start back an act	*/

        MasterFrame.isVisible = true
        PlayerFrame.isVisible = true

        /*TODO: give master frame and player frame the objects relative to*/

    }



    fun placeTokensOnMaps(tokens: MutableList<Element>){ //places tokens on both maps at corresponding points
        PlayerFrame.updateMap(tokens)
        MasterFrame.updateMap(tokens)
        repaintFrames()
    }
}


