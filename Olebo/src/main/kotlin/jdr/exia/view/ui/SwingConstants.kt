package jdr.exia.view.ui

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowSize
import java.awt.Dimension
import java.awt.event.KeyEvent

val DEFAULT_WINDOWS_SIZE = WindowSize(700.dp, 900.dp)
val DIMENSION_MAIN_WINDOW = Dimension(700, 900)
val DIMENSION_FRAME = Dimension(1920, 1080)

// Keys
const val CTRL = KeyEvent.CTRL_DOWN_MASK
const val CTRLSHIFT = CTRL or KeyEvent.SHIFT_DOWN_MASK

