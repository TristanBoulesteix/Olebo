package jdr.exia.view

import jdr.exia.localization.STR_PLAYER_TITLE_FRAME
import jdr.exia.localization.StringLocale
import jdr.exia.view.composable.master.MapPanel
import jdr.exia.view.tools.event.addKeyPressedListener
import jdr.exia.view.tools.screens
import jdr.exia.view.ui.MASTER_WINDOW_SIZE
import kotlinx.coroutines.*
import kotlinx.coroutines.swing.Swing
import java.awt.Dimension
import java.awt.GraphicsDevice
import java.awt.Window
import java.awt.event.KeyEvent
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.JDialog

/**
 * PlayerFrame is the Frame the Players can see, it shares its content with MasterFrame
 */
class PlayerDialog private constructor(mapPanel: MapPanel, private val onHide: () -> Unit, title: String) :
    JDialog(null as Window?) {
    companion object {
        private var playerDialog: PlayerDialog? = null

        fun toggle(data: PlayerDialogData, isVisible: Boolean) {
            playerDialog?.dispose()

            if (isVisible) {
                playerDialog = PlayerDialog(data.mapPanel, data.onHide, data.title).apply {
                    val currentScreenOfMasterWindow = data.getMasterWindowScreen()

                    // If there is only 1 screen, we display both frames there
                    if (screens.size == 1 || currentScreenOfMasterWindow == null) {
                        this.isUndecorated = false
                        this.isResizable = true
                        this.preferredSize = MASTER_WINDOW_SIZE.let { (height, width) ->
                            Dimension(width.value.toInt(), height.value.toInt())
                        }
                        this.pack()
                        this.setLocationRelativeTo(null)
                        this.isVisible = true
                    } else { //If 2 screens are present, we display the player frame in fullscreen on the 2nd screen
                        for (screen in screens) {
                            if (currentScreenOfMasterWindow != screen) {
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
                }
            } else {
                playerDialog = null
            }
        }

    }

    private val repaintJob: Job

    init {
        this.contentPane = mapPanel
        this.defaultCloseOperation = DO_NOTHING_ON_CLOSE

        this.title = title

        this.addWindowListener(object : WindowAdapter() {
            override fun windowClosing(e: WindowEvent) = dispose()
        })

        this.addKeyPressedListener {
            if (it.keyCode == KeyEvent.VK_ESCAPE) {
                dispose()
            }
        }

        repaintJob = CoroutineScope(Dispatchers.Swing).launch {
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
        val title: String,
        val mapPanel: MapPanel,
        val onHide: () -> Unit,
        val getMasterWindowScreen: () -> GraphicsDevice?
    )
}