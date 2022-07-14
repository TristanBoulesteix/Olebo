package fr.olebo.sharescene

import java.awt.Image
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.util.*
import javax.imageio.ImageIO

fun Base64Image(bufferedImage: BufferedImage, newSize: Int) =
    Base64Image(bufferedImage.toBase64String(newSize, newSize))

fun Base64Image(bufferedImage: BufferedImage, newHeight: Int, newWidth: Int) =
    Base64Image(bufferedImage.toBase64String(newWidth, newHeight))

private fun BufferedImage.toBase64String(width: Int, height: Int): String {
    val imageInByte = ByteArrayOutputStream().use {
        ImageIO.write(resize(width, height), "png", it)
        it.flush()
        it.toByteArray()
    }

    return String(Base64.getEncoder().encode(imageInByte))
}

private fun BufferedImage.resize(width: Int, height: Int): BufferedImage {
    val tmp: Image = getScaledInstance(width, height, Image.SCALE_SMOOTH)
    val resized = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
    val g2d = resized.createGraphics()
    g2d.drawImage(tmp, 0, 0, null)
    g2d.dispose()
    return resized
}