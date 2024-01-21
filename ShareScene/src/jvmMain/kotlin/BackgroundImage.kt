package fr.olebo.sharescene

import fr.olebo.utils.resize
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