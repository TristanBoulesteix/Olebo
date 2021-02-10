package jdr.exia.view.utils

import java.awt.Color
import java.awt.Dimension
import java.awt.Insets
import java.awt.event.KeyEvent
import javax.swing.BorderFactory
import javax.swing.border.Border

// Dimensions
val DIMENSION_MENU_FRAME = Dimension(600, 800)
val DIMENSION_FRAME = Dimension(1920, 1080)
val DIMENSION_BUTTON_DEFAULT = Dimension(150, 40)

// Colors
val BACKGROUND_COLOR_LIGHT_BLUE = Color(158, 195, 255)
val BACKGROUND_COLOR_ORANGE: Color = Color.ORANGE
val BACKGROUND_COLOR_SELECT_PANEL: Color = Color.LIGHT_GRAY

// Borders
val BORDER_BUTTONS: Border = BorderFactory.createEmptyBorder(15, 15, 15, 15)

// Keys
const val CTRL = KeyEvent.CTRL_DOWN_MASK
const val CTRLSHIFT = CTRL or KeyEvent.SHIFT_DOWN_MASK

// Insets
val DEFAULT_INSET = Insets(10, 150, 10, 10)