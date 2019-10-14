package jdr.exia.view.mainFrame

import jdr.exia.model.element.Element
import java.awt.GraphicsEnvironment
import java.io.File
import javax.imageio.ImageIO
import javax.swing.JDialog

/*PlayerFrame is the Frame the Players can see, it shares its content with MasterFrame
this is a singleton*/
object PlayerFrame : JDialog(), GameFrame {
    private val mapPanel = MapPanel()

    init {
        this.title = "Player"
        this.isResizable = false
        this.contentPane = mapPanel
        this.defaultCloseOperation = DISPOSE_ON_CLOSE


        val screens = GraphicsEnvironment.getLocalGraphicsEnvironment().screenDevices
        if (screens.size == 1) { //If there is only 1 screen, we display both frames there
            this.setSize(screens[0].displayMode.width,screens[0].displayMode.height)  //Sets the frame's size as exactly the size of the screen.
            
        } else { //If 2 screens are present, we display the player frame in fullscreen on the 2nd screen
            this.setSize(screens[1].displayMode.width,screens[1].displayMode.height)  //Sets the frame's size as exactly the size of the screen.
            this.isUndecorated = true
            this.pack()

            screens[1].fullScreenWindow = this
        }
        this.invalidate()
        this.repaint()



    }

    override fun updateMap(tokens: MutableList<Element>){
            mapPanel.updateTokens(tokens)

    }

    override fun setMapBackground(imageName: String) {
        mapPanel.backGroundImage = ImageIO.read( File(this.javaClass.getResource(imageName).path))

    }

}
