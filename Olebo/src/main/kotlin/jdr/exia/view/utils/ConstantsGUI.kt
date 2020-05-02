package jdr.exia.view.utils

import java.awt.Color
import java.awt.Dimension
import java.awt.Toolkit
import java.awt.event.KeyEvent
import javax.swing.BorderFactory
import javax.swing.border.Border

// Dimensions
val DIMENSION_FRAME = Dimension(600, 800)
val BORDER_BUTTONS: Border = BorderFactory.createEmptyBorder(15, 15, 15, 15)

// Colors
val BACKGROUND_COLOR_LIGHT_BLUE = Color(158, 195, 255)
val BACKGROUND_COLOR_ORANGE: Color = Color.ORANGE

// Key Event
val CTRL = Toolkit.getDefaultToolkit().menuShortcutKeyMaskEx
val CTRLSHIFT = CTRL or KeyEvent.SHIFT_DOWN_MASK