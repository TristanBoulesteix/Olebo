package fr.olebo.tests.components

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPlacement
import fr.olebo.components.Window
import fr.olebo.tests.applicationScope
import javax.swing.JFrame
import kotlin.properties.Delegates
import kotlin.test.Test
import kotlin.test.assertEquals

internal class WindowTests {
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun `create a window and update its dimensions`() = runComposeUiTest {
        val title = "test_window"
        val size = DpSize(800.dp, 600.dp)
        val minimumSize = DpSize(200.dp, 300.dp)
        val placement = WindowPlacement.Floating

        var awtWindow: JFrame by Delegates.notNull()

        setContent {
            applicationScope.Window(
                title = title,
                size = size,
                minimumSize = minimumSize,
                placement = placement
            ) {
                awtWindow = window
            }
        }

        assertEquals(awtWindow.title, title)
    }
}