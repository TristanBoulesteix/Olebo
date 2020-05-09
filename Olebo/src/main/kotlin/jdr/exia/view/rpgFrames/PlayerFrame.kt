package jdr.exia.view.rpgFrames

import jdr.exia.model.element.Element
import java.awt.GraphicsEnvironment
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.io.File
import javax.imageio.ImageIO
import javax.swing.JDialog

/**PlayerFrame is the Frame the Players can see, it shares its content with MasterFrame
this is a singleton*/
object PlayerFrame : JDialog(), GameFrame, KeyListener {
    val mapPanel = MapPanel()

    init {
        this.title = "Player"
        this.isUndecorated = true
        this.isResizable = false
        this.contentPane = mapPanel
        this.defaultCloseOperation = DISPOSE_ON_CLOSE
    }

    override fun setTitle(title: String?) {
        super.setTitle("Olebo - Fenêtre PJ - \"$title\"")
    }

    fun toggleDisplay() {
        if (isVisible) {
            isVisible = false
        } else {
            val className = System.getProperty("java.awt.graphicsenv")
            val ge = Class.forName(className).getDeclaredConstructor().newInstance() as GraphicsEnvironment
            val screens = ge.screenDevices
            if (screens.size == 1) { //If there is only 1 screen, we display both frames there
                this.setSize(screens[0].displayMode.width, screens[0].displayMode.height)  //Sets the frame's size as exactly the size of the screen.

            } else { //If 2 screens are present, we display the player frame in fullscreen on the 2nd screen
                this.setSize(
                        screens[1].displayMode.width,
                        screens[1].displayMode.height
                )  //Sets the frame's size as exactly the size of the screen.
                this.pack()

                screens[1].fullScreenWindow = this
            }
            isVisible = true
        }
    }

    override fun updateMap(tokens: MutableList<Element>) {
        mapPanel.updateTokens(tokens)
    }

    override fun setMapBackground(imageName: String) {
        mapPanel.backGroundImage = ImageIO.read(File(imageName))

    }

    override fun keyTyped(p0: KeyEvent?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun keyPressed(p0: KeyEvent?) {
        if (p0?.keyCode == KeyEvent.VK_ESCAPE) {
            toggleDisplay()
            println("wow")
        }
    }

    override fun keyReleased(p0: KeyEvent?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
