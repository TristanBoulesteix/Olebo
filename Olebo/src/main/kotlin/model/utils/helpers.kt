package model.utils

import model.act.Scene
import model.command.CommandManager
import model.element.Blueprint
import model.element.Element
import model.element.Type
import java.awt.Graphics
import java.awt.geom.AffineTransform
import java.awt.image.BufferedImage
import javax.swing.ImageIcon

/**
 * Convert an Int to the corresponding boolean
 *
 * @return true if the value of the integer is not 0
 */
fun Int.toBoolean() = this != 0

/**
 * Convert a Boolean to the corresponding integer
 *
 * @return 1 if true
 */
fun Boolean.toInt(): Int = if (this) 1 else 0

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
            BufferedImage.TYPE_INT_ARGB).apply {
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

fun <T> Scene?.callManager(value: T, func: (T, CommandManager) -> Unit) = this?.let { scene ->
    func(value, CommandManager(scene.id.value))
}

fun Scene?.callManager(func: (CommandManager) -> Unit) = this?.let { scene ->
    func(CommandManager(scene.id.value))
}

fun <T> Scene?.callManager(value: T, elements: Elements, func: (T, CommandManager, Elements) -> Unit) = this?.let { scene ->
    func(value, CommandManager(scene.id.value), elements)
}