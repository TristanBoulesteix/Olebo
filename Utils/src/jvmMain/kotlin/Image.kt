package fr.olebo.utils

import java.awt.Image
import java.awt.image.BufferedImage

fun BufferedImage.resize(width: Int, height: Int): BufferedImage {
    val tmp: Image = getScaledInstance(width, height, Image.SCALE_SMOOTH)
    val resized = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
    val g2d = resized.createGraphics()
    g2d.drawImage(tmp, 0, 0, null)
    g2d.dispose()
    return resized
}