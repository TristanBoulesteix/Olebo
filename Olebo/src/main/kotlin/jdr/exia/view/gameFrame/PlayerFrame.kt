package jdr.exia.view.gameFrame

import jdr.exia.model.element.Element
import jdr.exia.view.utils.setFullScreen
import java.awt.Dimension
import java.awt.GraphicsEnvironment
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.io.File
import javax.imageio.ImageIO
import javax.swing.JDialog

/**PlayerFrame is the Frame the Players can see, it shares its content with MasterFrame.
 *
 * This is a singletons
 * */
object PlayerFrame : JDialog(MasterFrame), GameFrame, KeyListener {
    val mapPanel = MapPanel()

    init {
        this.title = "Player"
        this.isUndecorated = true
        this.isResizable = false
        this.contentPane = mapPanel
        this.defaultCloseOperation = DISPOSE_ON_CLOSE
        this.setLocationRelativeTo(null)
    }

    fun toggleDisplay() {
        isVisible = if (isVisible) {
            false
        } else {
            val screens = GraphicsEnvironment.getLocalGraphicsEnvironment().screenDevices
            if (screens.size == 1) { //If there is only 1 screen, we display both frames there
                this.setFullScreen()
                this.size = Dimension(screens[0].displayMode.width, screens[0].displayMode.height)
            } else { //If 2 screens are present, we display the player frame in fullscreen on the 2nd screen
                screens.firstOrNull { it.iDstring != MasterFrame.graphicsConfiguration.device.iDstring }?.let {
                    this.size = Dimension(it.displayMode.width, it.displayMode.height)  //Sets the frame's size as exactly the size of the screen.
                    it.fullScreenWindow = this
                }
            }
            true
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
        }
    }

    override fun keyReleased(p0: KeyEvent?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
