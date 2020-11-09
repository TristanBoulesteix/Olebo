package view.utils.components

import java.awt.Font
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.RenderingHints
import javax.swing.JTextField

/**
 * A JTextField with placeholder
 */
class PlaceholderTextField(private var placeholder: String) : JTextField() {
    init {
        this.font = Font("Tahoma", Font.BOLD, 20)
    }

    override fun paintComponent(pG: Graphics) {
        super.paintComponent(pG)

        if (placeholder.isEmpty() || text.isNotEmpty()) return

        (pG as Graphics2D).apply {
            this.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
            )
            this.color = disabledTextColor
            this.drawString(
                placeholder, insets.left, pG.getFontMetrics()
                    .maxAscent + insets.top
            )
        }
    }
}


