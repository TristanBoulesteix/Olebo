package jdr.exia.view

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.currentCompositionLocalContext
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.window.AwtWindow
import androidx.compose.ui.window.LocalWindowExceptionHandlerFactory
import jdr.exia.localization.STR_PLAYER_TITLE_FRAME
import jdr.exia.localization.StringLocale
import jdr.exia.localization.get
import jdr.exia.model.dao.option.Settings
import jdr.exia.view.composable.master.ComposeMapPanel
import jdr.exia.view.composable.master.MapPanel
import jdr.exia.view.tools.event.addKeyPressedListener
import jdr.exia.view.tools.screens
import jdr.exia.view.ui.MASTER_WINDOW_SIZE
import jdr.exia.viewModel.MasterViewModel
import kotlinx.coroutines.*
import java.awt.Dimension
import java.awt.GraphicsDevice
import java.awt.GraphicsEnvironment
import java.awt.Window
import java.awt.event.KeyEvent
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.JDialog
import javax.swing.JFrame

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
                    if (!Settings.playerWindowShouldBeFullScreen || screens.size == 1 || currentScreenOfMasterWindow == null) {
                        this.isUndecorated = false
                        this.isResizable = true
                        this.preferredSize = MASTER_WINDOW_SIZE.let { (height, width) ->
                            Dimension(width.value.toInt(), height.value.toInt())
                        }
                        this.bounds = GraphicsEnvironment.getLocalGraphicsEnvironment().maximumWindowBounds
                        this.setLocationRelativeTo(null)
                        this.isVisible = true
                    } else { //If 2 screens are present, we display the player frame in fullscreen on the 2nd screen
                        for (screen in screens) {
                            if (currentScreenOfMasterWindow != screen) {
                                // Sets the frame's size as exactly the size of the screen.
                                this.isUndecorated = true
                                this.isResizable = false

                                screen.fullScreenWindow = this

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

        repaintJob = CoroutineScope(Dispatchers.Main).launch {
            while (isActive) {
                mapPanel.repaint()
                // The delay must be greater than or equal to 120.
                // Since the repaint method of MapPanel last on average 120 milliseconds, if the delay is inferior, the Swing thread can be slowed down or locked
                delay(120L)
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

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PlayerDialog(
    viewModel: MasterViewModel,
    actName: String,
    onDispose: (ComposeWindow) -> Unit,
    getMasterWindowScreen: () -> GraphicsDevice?
) {
    val compositionLocalContext by rememberUpdatedState(currentCompositionLocalContext)
    val windowExceptionHandlerFactory by rememberUpdatedState(
        LocalWindowExceptionHandlerFactory.current
    )



    AwtWindow(
        create = {
            ComposeWindow().apply ComposeWindow@{
                this.compositionLocalContext = compositionLocalContext
                this.exceptionHandler = windowExceptionHandlerFactory.exceptionHandler(this)

                defaultCloseOperation = JDialog.DISPOSE_ON_CLOSE

                addWindowListener(object : WindowAdapter() {
                    override fun windowClosed(e: WindowEvent) {
                        onDispose(this@ComposeWindow)
                    }
                })

                val currentScreenOfMasterWindow = getMasterWindowScreen()

                // If there is only 1 screen, we display both frames there
                if (!Settings.playerWindowShouldBeFullScreen || screens.size == 1 || currentScreenOfMasterWindow == null) {
                    this.preferredSize = MASTER_WINDOW_SIZE.let { (height, width) ->
                        Dimension(width.value.toInt(), height.value.toInt())
                    }
                    this.bounds = GraphicsEnvironment.getLocalGraphicsEnvironment().maximumWindowBounds
                    this.setLocationRelativeTo(null)
                    this.isVisible = true
                } else { //If 2 screens are present, we display the player frame in fullscreen on the 2nd screen
                    screens.firstOrNull { it != currentScreenOfMasterWindow }?.let { screen ->
                        val screenBounds = screen.defaultConfiguration.bounds

                        setLocation(
                            ((screenBounds.width / 2) - (size.width / 2)) + screenBounds.x,
                            ((screenBounds.height / 2) - (size.height / 2)) + screenBounds.y
                        )

                        extendedState = extendedState or JFrame.MAXIMIZED_BOTH
                        isUndecorated = true
                    }
                }

                setContent(onPreviewKeyEvent = { false }, onKeyEvent = {
                    if (it.key == Key.Escape) {
                        dispose()
                    }
                    false
                }) {
                    ComposeMapPanel(Modifier.fillMaxSize(), viewModel)
                }
            }
        },
        dispose = Window::dispose,
        update = {
            it.compositionLocalContext = compositionLocalContext
            it.exceptionHandler = windowExceptionHandlerFactory.exceptionHandler(it)

            it.title = "Olebo - ${StringLocale[STR_PLAYER_TITLE_FRAME]} - $actName"

            if (!it.isDisplayable) {
                it.makeDisplayable()
                it.contentPane.paint(it.graphics)
            }
        }
    )
}

private fun Window.makeDisplayable() {
    val oldPreferredSize = preferredSize
    preferredSize = size
    try {
        pack()
    } finally {
        preferredSize = oldPreferredSize
    }
}