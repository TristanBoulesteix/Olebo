package jdr.exia.view

import jdr.exia.localization.STR_PLAYER_TITLE_FRAME
import jdr.exia.localization.StringLocale
import jdr.exia.view.composable.master.MapPanel
import jdr.exia.view.tools.DefaultFunction
import jdr.exia.view.tools.event.addKeyPressedListener
import jdr.exia.view.tools.screens
import jdr.exia.view.ui.DIMENSION_FRAME
import kotlinx.coroutines.*
import kotlinx.coroutines.swing.Swing
import java.awt.GraphicsDevice
import java.awt.Window
import java.awt.event.KeyEvent
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.JDialog

/**
 * PlayerFrame is the Frame the Players can see, it shares its content with MasterFrame
 */
class PlayerDialog private constructor(mapPanel: MapPanel, private val onHide: DefaultFunction) :
    JDialog(null as Window?) {
    companion object {
        private var playerDialog: PlayerDialog? = null

        fun toggle(data: PlayerDialogData, isVisible: Boolean) {
            playerDialog?.dispose()

            if (isVisible) {
                PlayerDialog(data.mapPanel, data.onHide).apply {
                    // If there is only 1 screen, we display both frames there
                    if (screens.size == 1) {
                        this.isUndecorated = false
                        this.isResizable = true
                        this.preferredSize = DIMENSION_FRAME
                        this.pack()
                        this.setLocationRelativeTo(null)
                        this.isVisible = true
                    } else { //If 2 screens are present, we display the player frame in fullscreen on the 2nd screen
                        for (screen in screens) {
                            if (data.getMasterWindowScreen() != screen) {
                                // Sets the frame's size as exactly the size of the screen.
                                this.setSize(
                                    screen.displayMode.width,
                                    screen.displayMode.height
                                )

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

                    playerDialog = this
                }
            } else {
                playerDialog = null
            }
        }

        val isVisible
            get() = playerDialog?.isVisible == true
    }

    private val repaintJob: Job

    init {
        this.contentPane = mapPanel
        this.defaultCloseOperation = DO_NOTHING_ON_CLOSE

        this.addWindowListener(object : WindowAdapter() {
            override fun windowClosing(e: WindowEvent) = dispose()
        })

        this.addKeyPressedListener {
            if (it.keyCode == KeyEvent.VK_ESCAPE) {
                dispose()
            }
        }

        repaintJob = GlobalScope.launch(Dispatchers.Swing) {
            while (isActive) {
                mapPanel.repaint()
                delay(80L)
            }
        }
    }

    override fun setTitle(title: String) =
        super.setTitle("Olebo - ${StringLocale[STR_PLAYER_TITLE_FRAME]} - \"$title\"")

    override fun dispose() {
        super.dispose()
        repaintJob.cancel()
        onHide()
    }

    data class PlayerDialogData(
        val mapPanel: MapPanel,
        val onHide: DefaultFunction,
        val getMasterWindowScreen: () -> GraphicsDevice
    )
}