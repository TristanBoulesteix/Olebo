package jdr.exia.view

import java.awt.GraphicsEnvironment
import java.awt.Image
import javax.swing.JFrame

/*PlayerFrame is the Frame the Players can see, it shares its content with MasterFrame
this is a singleton*/
object PlayerFrame : JFrame() {
    private val mapPanel = MapPanel()

    init {

        val screens = GraphicsEnvironment.getLocalGraphicsEnvironment().screenDevices
        if (screens.size == 1) { //If there is only 1 screen, we display both frames there
            this.setSize(screens[0].displayMode.width,screens[0].displayMode.height)  //Sets the frame's size as exactly the size of the screen.
        } else { //If 2 screens are present, we display the player frame in fullscreen on the 2nd screen
            this.setSize(screens[1].displayMode.width,screens[1].displayMode.height)  //Sets the frame's size as exactly the size of the screen.
            this.isUndecorated = true
            screens[1].fullScreenWindow = this
        }

        this.title = "Player"
        this.isResizable = false
        this.contentPane = mapPanel
        this.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        this.extendedState = JFrame.MAXIMIZED_BOTH

    }

    fun placeElementOnMap(tokens: MutableList<ElementPlaceHolder>){
            this.mapPanel.updateTokens(tokens)

    }

}
