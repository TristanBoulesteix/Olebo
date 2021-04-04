package jdr.exia.view.compose.tools

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.IntSize

class SizeAdjuster {
    private var currentSize: IntSize? = null
        set(value) {
            field = value
            size = field ?: IntSize.Zero
        }

    var size by mutableStateOf(currentSize ?: IntSize.Zero)
        private set

    fun componentSizeListener(size: IntSize) {
        currentSize = size
    }
}