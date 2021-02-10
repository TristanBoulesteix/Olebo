package jdr.exia.model.utils

import jdr.exia.model.act.Scene
import jdr.exia.model.command.CommandManager
import jdr.exia.model.dao.option.Color
import jdr.exia.model.element.Blueprint
import jdr.exia.model.element.Element
import jdr.exia.model.element.Type
import java.awt.Graphics
import java.awt.geom.AffineTransform
import java.awt.image.BufferedImage
import javax.swing.ImageIcon
import java.awt.Color as JColor

/**
 * Convert a String to the corresponding boolean
 *
 * @return true if the value is "true"
 */
fun String?.toBoolean(): Boolean = this?.toLowerCase() == "true"

/**
 * Check if element is a PNJ or a PJ
 *
 * @return true if it's a character
 */
fun Element?.isCharacter(): Boolean {
    return this != null && (this.type.typeElement == Type.PNJ || this.type.typeElement == Type.PJ)
}

/**
 * Check if blueprint is a PNJ or a PJ
 *
 * @return true if it's a character
 */
fun Blueprint?.isCharacter(): Boolean {
    return this != null && (this.type.typeElement == Type.PNJ || this.type.typeElement == Type.PJ)
}

fun ImageIcon.rotate(degs: Double) = with(BufferedImage(this.iconWidth, this.iconHeight, BufferedImage.TYPE_INT_ARGB)) {
    fun ImageIcon.toBufferedImage() = BufferedImage(
        this.iconWidth,
        this.iconHeight,
        BufferedImage.TYPE_INT_ARGB
    ).apply {
        val g: Graphics = createGraphics()
        paintIcon(null, g, 0, 0)
        g.dispose()
    }

    createGraphics().apply {
        val transform = AffineTransform()
        transform.rotate(degs / 180 * Math.PI, (iconWidth / 2).toDouble(), (iconHeight / 2).toDouble())
        drawRenderedImage(toBufferedImage(), transform)
        dispose()
    }
    ImageIcon(this)
}

inline fun Scene?.callManager(elements: Elements, func: (CommandManager, Elements) -> Unit) = this?.let { scene ->
    func(CommandManager(scene.id.value), elements)
}

inline fun <T> Scene?.callManager(value: T, elements: Elements, func: (T, CommandManager, Elements) -> Unit) =
    this?.let { scene ->
        func(value, CommandManager(scene.id.value), elements)
    }

/**
 * Convert a [java.awt.Color] to a [Color]
 */
fun JColor.toColor() = this.let { Color(it.red, it.green, it.blue) }

/**
 * Convert a [Color] to a [java.awt.Color]
 */
fun Color.toJColor() = this.let { JColor(it.r, it.g, it.b) }