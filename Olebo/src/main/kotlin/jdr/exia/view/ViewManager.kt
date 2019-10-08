package jdr.exia.view

import java.awt.GraphicsDevice
import java.awt.GraphicsEnvironment
import javax.swing.JFrame

/*ViewManager is View's facade
this is a singleton*/
object ViewManager {
    private var playerFrame = PlayerFrame

    private var masterFrame = MasterFrame

    /* these 2 lines generate a GraphicsDevice array, GraphicsDevice are screens*/
    private val screens: Array<GraphicsDevice>
        get() {
            val graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment()
            return graphicsEnvironment.screenDevices
        }

    /* Code supposed to be in Controller, but due to the non-existence of the Controller, the view is kindly sheltering these lines of code */
    private val clickedElement: Any? =
        null //this is the last Element that was clicked on, if it is equal to NULL, the program tries to fill this slot with an object

    init {
        this.initializeActFrames()
    }


    fun refreshFrames() {  // Refreshes Player and MasterFrames at once
        masterFrame.mapPanel.refresh()
    }

    private fun initializeActFrames() { /*this method activates the Player and GM frames to initiate/start back an act	*/
        val screens = this.screens

        /* if there are 2 screens or more, the the 1st one is GM's screen, and the 2nd
		is the players' screen.*/
        when {
            screens.size >= 2 -> {
                playerFrame.isUndecorated = true
                masterFrame.isUndecorated = true
                screens[1].fullScreenWindow = playerFrame
                screens[0].fullScreenWindow = masterFrame
                masterFrame.extendedState = JFrame.MAXIMIZED_BOTH
                playerFrame.extendedState = JFrame.MAXIMIZED_BOTH

                /* if there's only 1 screen, we assume the act is played for testing purposes,
                and we make both frames decorated*/
            }
            screens.size == 1 -> {

                playerFrame.isUndecorated = false
                masterFrame.isUndecorated = false
                playerFrame.isVisible = true
                masterFrame.isVisible = true
                masterFrame.extendedState = JFrame.MAXIMIZED_BOTH
                playerFrame.extendedState = JFrame.MAXIMIZED_BOTH

            }
            else -> throw RuntimeException("No Screens Found")
        }

    }

    fun placeElement(element: ElementPlaceHolder){

    }

}


