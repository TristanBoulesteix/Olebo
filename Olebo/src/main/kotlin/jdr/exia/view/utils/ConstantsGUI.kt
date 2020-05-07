package jdr.exia.view.utils

import java.awt.Color
import java.awt.Dimension
import java.awt.Toolkit
import java.awt.event.KeyEvent
import javax.swing.BorderFactory
import javax.swing.border.Border

// Dimensions
val DIMENSION_FRAME = Dimension(600, 800)

// Colors
val BACKGROUND_COLOR_LIGHT_BLUE = Color(158, 195, 255)
val BACKGROUND_COLOR_ORANGE: Color = Color.ORANGE
val BACKGROUND_COLOR_SELECT_PANEL: Color = Color.LIGHT_GRAY

// Borders
const val DEFAULT_BORDER_SIZE = 2
val RIGHT_BORDER_BLACK: Border = BorderFactory.createMatteBorder(0, 0, 0, DEFAULT_BORDER_SIZE, Color.BLACK)
val BORDER_BUTTONS: Border = BorderFactory.createEmptyBorder(15, 15, 15, 15)

// Key Event
val CTRL = Toolkit.getDefaultToolkit().menuShortcutKeyMask
val CTRLSHIFT = CTRL or KeyEvent.SHIFT_DOWN_MASK