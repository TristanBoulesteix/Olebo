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
import kotlinx.coroutines.delay
import kotlin.system.exitProcess

@Composable
fun SplashScreen(onDone: () -> Unit, loadingAction: suspend SplashScreenActionScope.() -> Unit) {
    var state by remember { mutableStateOf(SplashScreenState(80f, "", isError = false)) }

    LaunchedEffect(Unit) {
        val actionScope = object : SplashScreenActionScope {
            override fun setStatus(percentage: Float, description: String, isError: Boolean) {
                state = SplashScreenState(percentage, description, isError)
            }
        }

        actionScope.loadingAction()

        delay(10000)
        exitProcess(0)
        onDone()
    }

    SplashScreenUI(state)
}

@Immutable
interface SplashScreenActionScope {
    fun setStatus(percentage: Float, description: String, isError: Boolean = false)
}

@Immutable
private data class SplashScreenState(val percentage: Float, val description: String, val isError: Boolean)

@Composable
private fun SplashScreenUI(state: SplashScreenState) = DialogWindow(
    onCloseRequest = {},
    undecorated = true,
    resizable = false,
    focusable = false,
    state = rememberDialogState(height = 200.dp)
) {
    Box {
        Image(
            bitmap = useResource("splashscreen/splashscreen_image.jpg", ::loadImageBitmap),
            contentDescription = "splashscreen"
        )
        Column(Modifier.align(Alignment.BottomStart)) {
            Text(state.description)
            LinearProgressIndicator(state.percentage, modifier = Modifier.height(10.dp), color = if(state.isError) Color.Red else Color(0xFFddaf61))
        }
    }
}