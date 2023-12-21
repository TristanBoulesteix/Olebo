package jdr.exia

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.useResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.rememberDialogState
import jdr.exia.view.ui.LocalLogger

@Composable
fun SplashScreen(onDone: () -> Unit, loadingAction: suspend SplashScreenActionScope.() -> Unit) {
    var state by remember { mutableStateOf(SplashScreenState(0f, "", isError = false)) }

    val logger = LocalLogger.current

    LaunchedEffect(Unit) {
        val actionScope = object : SplashScreenActionScope {
            override fun setStatus(percentage: Float, description: String, isError: Boolean) {
                state = SplashScreenState(if (percentage < 0) state.percentage else percentage, description, isError)
                logger.info(description)
            }
        }

        actionScope.loadingAction()

        onDone()
    }

    SplashScreenUI(state)
}

@Immutable
interface SplashScreenActionScope {
    fun setStatus(description: String, isError: Boolean = false) = setStatus(-1f, description, isError)

    fun setStatus(percentage: Float, description: String, isError: Boolean = false)
}

@Immutable
private data class SplashScreenState(val percentage: Float, val description: String, val isError: Boolean)

@Composable
private fun SplashScreenUI(state: SplashScreenState) = DialogWindow(
    onCloseRequest = {},
    undecorated = true,
    resizable = false,
    focusable = true,
    state = rememberDialogState(height = 200.dp)
) {
    Box {
        Image(
            bitmap = useResource("splashscreen/splashscreen_image.jpg", ::loadImageBitmap),
            contentDescription = "splashscreen"
        )
        Column(Modifier.align(Alignment.BottomStart)) {
            Text(state.description)
            LinearProgressIndicator(
                state.percentage,
                modifier = Modifier.height(10.dp),
                color = if (state.isError) Color.Red else Color(0xFFddaf61)
            )
        }
    }
}