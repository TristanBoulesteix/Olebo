package jdr.exia.view.windows

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
import jdr.exia.view.tools.screens
import jdr.exia.view.ui.MASTER_WINDOW_SIZE
import jdr.exia.viewModel.MasterViewModel
import java.awt.Dimension
import java.awt.GraphicsDevice
import java.awt.GraphicsEnvironment
import java.awt.Window
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.JDialog
import javax.swing.JFrame

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
                    ComposeMapPanel(Modifier.fillMaxSize(), viewModel, isMasterWindow = false)
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