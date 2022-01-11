package fr.olebo.sharescene.map

import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.util.*
import javax.imageio.ImageIO

fun Base64Image(bufferedImage: BufferedImage) = Base64Image(bufferedImage.toBase64String())

private fun BufferedImage.toBase64String(): String {
    val imageInByte = ByteArrayOutputStream().use {
        ImageIO.write(this, "jpg", it)
        it.flush()
        it.toByteArray()
    }

    return String(Base64.getEncoder().encode(imageInByte))
}