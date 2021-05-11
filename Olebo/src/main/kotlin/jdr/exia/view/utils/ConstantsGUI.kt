package jdr.exia.view.utils

import java.awt.Color
import java.awt.Dimension
import java.awt.Insets
import java.awt.event.KeyEvent

val DIMENSION_FRAME = Dimension(1920, 1080)
val DIMENSION_BUTTON_DEFAULT = Dimension(150, 40)

val BACKGROUND_COLOR_SELECT_PANEL: Color = Color.LIGHT_GRAY

// Keys
const val CTRL = KeyEvent.CTRL_DOWN_MASK
const val CTRLSHIFT = CTRL or KeyEvent.SHIFT_DOWN_MASK

// Insets
val DEFAULT_INSET = Insets(10, 150, 10, 10)