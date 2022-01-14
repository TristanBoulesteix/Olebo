package fr.olebo.sharescene

import java.awt.Image
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.util.*
import javax.imageio.ImageIO

fun Base64Image(bufferedImage: BufferedImage, resize: Boolean = true) =
    Base64Image(bufferedImage.toBase64String(resize))

private fun BufferedImage.toBase64String(resize: Boolean): String {
    val imageInByte = ByteArrayOutputStream().use {
        ImageIO.write(if (resize) resize(50, 50) else this, "png", it)
        it.flush()
        it.toByteArray()
    }

    return String(Base64.getEncoder().encode(imageInByte))
}

private fun BufferedImage.resize(height: Int, width: Int): BufferedImage {
    val tmp: Image = getScaledInstance(width, height, Image.SCALE_SMOOTH)
    val resized = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
    val g2d = resized.createGraphics()
    g2d.drawImage(tmp, 0, 0, null)
    g2d.dispose()
    return resized
}