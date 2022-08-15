package jdr.exia

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.drop

object DeveloperModeManager {
    private val isEnabled = MutableStateFlow(false)

    val enabledState
        @Composable
        get() = isEnabled.collectAsState()

    val enabledFlow
        get() = isEnabled.drop(1)

    suspend fun toggle() {
        isEnabled.emit(!isEnabled.value)
    }
}