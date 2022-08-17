package jdr.exia

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.drop

object DeveloperModeManager {
    private val isEnabledFlow = MutableStateFlow(false)

    val enabledState
        @Composable
        get() = isEnabledFlow.collectAsState()

    val enabledFlow
        get() = isEnabledFlow.drop(1)

    val isCurrentlyEnabled
        get() = isEnabledFlow.value

    suspend fun toggle() {
        isEnabledFlow.emit(!isEnabledFlow.value)
    }
}