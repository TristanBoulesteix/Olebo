package view.frames.rpg

import model.element.Element
import view.utils.DIMENSION_FRAME
import java.awt.GraphicsEnvironment
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.io.File
import javax.imageio.ImageIO
import javax.swing.JDialog

/**
 * PlayerFrame is the Frame the Players can see, it shares its content with MasterFrame
 */
class PlayerFrame private constructor() : JDialog(), GameFrame, KeyListener {
    companion object {
        private var playerFrameInstance: PlayerFrame? = null

        var mapBackground: String = ""
            set(value) {
                field = value
                playerFrameInstance?.setMapBackground(value)
            }

        var title = "Player"
            set(value) {
                playerFrameInstance?.title = value
                field = value
            }

        var map = mutableListOf<Element>()
            set(value) {
                playerFrameInstance?.updateMap(value)
                field = value
            }

        fun show() {
            playerFrameInstance = PlayerFrame().apply {
                this.title = Companion.title
                this.setMapBackground(mapBackground)
                this.updateMap(map)
                GraphicsEnvironment.getLocalGraphicsEnvironment().let { ge ->
                    val screens = ge.screenDevices
                    if (screens.size == 1) { //If there is only 1 screen, we display both frames there
                        this.isUndecorated = false
                        this.isResizable = true
                        this.preferredSize = DIMENSION_FRAME
                        this.pack()
                        this.setLocationRelativeTo(null)
                        isVisible = true
                    } else { //If 2 screens are present, we display the player frame in fullscreen on the 2nd screen
                        for (screen in screens) {
                            if (MasterFrame.graphicsConfiguration.device != screen) {
                                this.setSize(
                                        screen.displayMode.width,
                                        screen.displayMode.height
                                )  //Sets the frame's size as exactly the size of the screen.
                                this.isUndecorated = true
                                this.isResizable = false

                                this.pack()
                                screen.fullScreenWindow = this
                                this.location = screen.defaultConfiguration.bounds.location.apply {
                                    x *= screen.defaultConfiguration.defaultTransform.scaleX.toInt()
                                    y *= screen.defaultConfiguration.defaultTransform.scaleY.toInt()
                                }
                                break
                            }
                        }
                    }
                }
            }
        }

        fun hide() {
            playerFrameInstance?.dispose()
            playerFrameInstance = null
        }

        fun repaint() = playerFrameInstance?.repaint()
    }

    private val mapPanel = MapPanel()

    init {
        this.contentPane = mapPanel
        this.addKeyListener(this)
        this.defaultCloseOperation = DO_NOTHING_ON_CLOSE
        this.addWindowListener(object : WindowAdapter() {
            override fun windowClosing(e: WindowEvent) = Companion.hide().also { MasterMenuBar.togglePlayerFrameMenuItem?.isSelected = false }
        })
    }

    override fun setTitle(title: String) =
            super.setTitle("Olebo - Fenêtre PJ - \"$title\"")

    override fun updateMap(tokens: MutableList<Element>) {
        mapPanel.updateTokens(tokens)
    }

    override fun setMapBackground(imageName: String) {
        mapPanel.backGroundImage = ImageIO.read(File(imageName))
    }

    override fun keyTyped(p0: KeyEvent) {
    }

    override fun keyPressed(p0: KeyEvent) {
        if (p0.keyCode == KeyEvent.VK_ESCAPE) {
            Companion.hide()
            MasterMenuBar.togglePlayerFrameMenuItem?.isSelected = false
        }
    }

    override fun keyReleased(p0: KeyEvent) {
    }

}
