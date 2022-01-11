package jdr.exia.model.tools

import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.util.*
import javax.imageio.ImageIO

fun BufferedImage.toBase64String(): String {
    val imageInByte = ByteArrayOutputStream().use {
        ImageIO.write(this, "jpg", it)
        it.flush()
        it.toByteArray()
    }

    return String(Base64.getEncoder().encode(imageInByte))
}