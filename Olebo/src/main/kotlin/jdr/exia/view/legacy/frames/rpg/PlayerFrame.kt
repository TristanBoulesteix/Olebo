package jdr.exia.view.legacy.frames.rpg

import jdr.exia.localization.STR_PLAYER_TITLE_FRAME
import jdr.exia.localization.StringLocale
import jdr.exia.model.element.Elements
import jdr.exia.model.element.emptyElements
import jdr.exia.view.legacy.frames.Reloadable
import jdr.exia.view.legacy.utils.components.MasterMenuBar
import jdr.exia.view.ui.DIMENSION_FRAME
import jdr.exia.view.legacy.utils.event.addKeyPressedListener
import jdr.exia.view.tools.screens
import java.awt.Color
import java.awt.Image
import java.awt.Window
import java.awt.event.KeyEvent
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.JDialog

/**
 * PlayerFrame is the Frame the Players can see, it shares its content with MasterFrame
 */
class PlayerFrame private constructor() : JDialog(null as Window?), GameFrame {
    companion object : Reloadable {
        private var playerFrameInstance: PlayerFrame? = null

        var mapBackground: Image? = null
            set(value) {
                field = value
                playerFrameInstance?.mapBackground = value
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
                this.mapBackground = this@Companion.mapBackground
                this.updateMap(map)
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
        this.defaultCloseOperation = DO_NOTHING_ON_CLOSE
        this.addWindowListener(object : WindowAdapter() {
            override fun windowClosing(e: WindowEvent) =
                Companion.hide().also { MasterMenuBar.togglePlayerFrameMenuItem?.isSelected = false }
        })
        this.addKeyPressedListener {
            if (it.keyCode == KeyEvent.VK_ESCAPE) {
                Companion.hide()
                MasterMenuBar.togglePlayerFrameMenuItem?.isSelected = false
            }
        }
    }

    override fun reload() = repaint()

    override fun setTitle(title: String) =
        super.setTitle("Olebo - ${StringLocale[STR_PLAYER_TITLE_FRAME]} - \"$title\"")

    override fun updateMap(tokens: Elements) {
        mapPanel.updateTokens(tokens)
    }

    override var mapBackground by mapPanel::backGroundImage

    override fun dispose() {
        this.mapPanel.repaintJob?.cancel()
        super.dispose()
    }
}
