package view.utils

import java.awt.Color
import java.awt.Dimension
import java.awt.Toolkit
import java.awt.event.KeyEvent
import javax.swing.BorderFactory
import javax.swing.border.Border

// Dimensions
val DIMENSION_MENU_FRAME = Dimension(600, 800)
val DIMENSION_FRAME = Dimension(1920, 1080)

// Colors
val BACKGROUND_COLOR_LIGHT_BLUE = Color(158, 195, 255)
val BACKGROUND_COLOR_ORANGE: Color = Color.ORANGE
val BACKGROUND_COLOR_SELECT_PANEL: Color = Color.LIGHT_GRAY

// Borders
const val DEFAULT_BORDER_SIZE = 2
val MARGIN_LEFT: Border = BorderFactory.createEmptyBorder(0, 10, 0, 0)
val BORDER_BUTTONS: Border = BorderFactory.createEmptyBorder(15, 15, 15, 15)

// Keys
val CTRL = Toolkit.getDefaultToolkit().menuShortcutKeyMask
val CTRLSHIFT = CTRL or KeyEvent.SHIFT_DOWN_MASK