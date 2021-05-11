package jdr.exia.view.utils.components.templates

import java.awt.Font
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.RenderingHints
import javax.swing.JTextField

/**
 * A JTextField with placeholder
 */
class PlaceholderTextField(private var placeholder: String, fontSize: Int = 20) : JTextField() {
    init {
        this.font = Font("Tahoma", Font.BOLD, fontSize)
    }

    override fun paintComponent(pG: Graphics) {
        super.paintComponent(pG)

        if (placeholder.isEmpty() || text.isNotEmpty()) return

        (pG as Graphics2D).setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON
        )
        pG.color = disabledTextColor
        pG.drawString(
            placeholder, insets.left, pG.getFontMetrics()
                .maxAscent + insets.top
        )
    }
}


