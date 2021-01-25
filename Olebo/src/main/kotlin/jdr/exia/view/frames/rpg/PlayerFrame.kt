package jdr.exia.view.frames.rpg

import jdr.exia.localization.STR_PLAYER_TITLE_FRAME
import jdr.exia.localization.Strings
import jdr.exia.model.utils.Elements
import jdr.exia.model.utils.emptyElements
import jdr.exia.view.frames.Reloadable
import jdr.exia.view.utils.DIMENSION_FRAME
import java.awt.Color
import java.awt.GraphicsEnvironment
import java.awt.Window
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
class PlayerFrame private constructor() : JDialog(null as Window?), GameFrame, KeyListener {
    companion object : Reloadable {
        private var playerFrameInstance: PlayerFrame? = null

        var mapBackground: String = ""
            set(value) {
                field = value
                playerFrameInstance?.setMapBackground(value)
            }

        var title = "\" \""
            set(value) {
                playerFrameInstance?.title = value
                field = value
            }

        var map = emptyElements()
            set(value) {
                playerFrameInstance?.updateMap(value)
                field = value
            }

        fun toggle(isVisble: Boolean) = if (isVisble) show() else hide()

        private fun show() {
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
                        this.isVisible = true
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
                                    with(screen.defaultConfiguration.defaultTransform) {
                                        x *= scaleX.toInt()
                                        y *= scaleY.toInt()
                                    }
                                }
                                break
                            }
                        }
                    }
                }
            }
        }

        fun updateCursor(contentColor: Color, borderColor: Color) {
            playerFrameInstance?.mapPanel?.cursorColor = contentColor
            playerFrameInstance?.mapPanel?.borderCursorColor = borderColor
        }

        fun hide() {
            playerFrameInstance?.dispose()
            playerFrameInstance = null
        }

        override fun reload() {
            playerFrameInstance?.reload()
        }
    }

    private val mapPanel = MapPanel(this)

    init {
        this.contentPane = mapPanel
        this.addKeyListener(this)
        this.defaultCloseOperation = DO_NOTHING_ON_CLOSE
        this.addWindowListener(object : WindowAdapter() {
            override fun windowClosing(e: WindowEvent) =
                Companion.hide().also { MasterMenuBar.togglePlayerFrameMenuItem?.isSelected = false }
        })
    }

    override fun reload() = repaint()

    override fun setTitle(title: String) = super.setTitle("Olebo - ${Strings[STR_PLAYER_TITLE_FRAME]} - \"$title\"")

    override fun updateMap(tokens: Elements) {
        mapPanel.updateTokens(tokens)
    }

    override fun setMapBackground(imageName: String) {
        mapPanel.backGroundImage = ImageIO.read(File(imageName))
    }

    override fun keyTyped(p0: KeyEvent) = Unit

    override fun keyPressed(p0: KeyEvent) {
        if (p0.keyCode == KeyEvent.VK_ESCAPE) {
            Companion.hide()
            MasterMenuBar.togglePlayerFrameMenuItem?.isSelected = false
        }
    }

    override fun keyReleased(p0: KeyEvent) = Unit
}