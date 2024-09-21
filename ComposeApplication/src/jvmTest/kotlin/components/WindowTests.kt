package fr.olebo.tests.components

import androidx.compose.runtime.SideEffect
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import fr.olebo.components.window.OleboWindow
import fr.olebo.components.window.toAwtDimension
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

        var awtWindow: JFrame by Delegates.notNull()

        setContent {
            applicationScope.OleboWindow(
                title = title,
                size = size,
                minimumSize = minimumSize
            ) {
                SideEffect { awtWindow = window }
            }
        }

        assertEquals(awtWindow.title, title)
        assertEquals(awtWindow.size, size.toAwtDimension())
        assertEquals(awtWindow.minimumSize, minimumSize.toAwtDimension())
    }

    @Test
    fun `convert DpSize to awt dimension`(){
        val dimension = DpSize(800.dp, 600.dp).toAwtDimension()

        assertEquals(dimension.width, 800)
        assertEquals(dimension.height, 600)
    }
}