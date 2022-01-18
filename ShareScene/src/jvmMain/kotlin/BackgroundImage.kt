package fr.olebo.sharescene

import java.awt.Image
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.util.*
import javax.imageio.ImageIO

fun Base64Image(bufferedImage: BufferedImage, newSize: Int? = null) =
    Base64Image(bufferedImage.toBase64String(newSize))

private fun BufferedImage.toBase64String(newSize: Int?): String {
    val imageInByte = ByteArrayOutputStream().use {
        ImageIO.write(if (newSize != null) resize(newSize) else this, "png", it)
        it.flush()
        it.toByteArray()
    }

    return String(Base64.getEncoder().encode(imageInByte))
}

private fun BufferedImage.resize(size: Int): BufferedImage {
    val tmp: Image = getScaledInstance(size, size, Image.SCALE_SMOOTH)
    val resized = BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB)
    val g2d = resized.createGraphics()
    g2d.drawImage(tmp, 0, 0, null)
    g2d.dispose()
    return resized
}