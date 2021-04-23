package jdr.exia.model.tools

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.imageFromResource
import jdr.exia.model.dao.OLEBO_DIRECTORY
import java.io.File
import org.jetbrains.skija.Image as SkijaImage

private val imgPath = OLEBO_DIRECTORY + "img${File.separator}"

//TODO: Replace with value class when Kotlin 1.5 will be available (if Jetpack compose is compatible)
@Immutable
inline class Image(val path: String) {
    companion object {
        @Stable
        val unspecified = Image("")
    }

   fun isValid() = !isUnspecified() && File(path).let { it.exists() && it.isFile }

    fun isUnspecified() = path.isBlank()
}

fun imageFromIconRes(name: String) = imageFromResource("icons/$name.png")

fun imageFromFile(file: File): ImageBitmap {
    return SkijaImage.makeFromEncoded(file.readBytes()).asImageBitmap()
}

/**
 * Save a picture to img folder
 *
 * @param path The path of the picture to save
 */
fun saveImg(path: String): String {
    val img = File.createTempFile(
        "img_",
        "_background.png",
        File(imgPath).apply { this.mkdirs() }
    )

    File(path).copyTo(img, true)

    return img.absolutePath
}

/**
 * Save a picture to img folder
 *
 * @param path The path of the picture to save
 */
fun savePathToImage(path: String, suffix: String = "background"): Image {
    val img = File.createTempFile(
        "img_",
        "_$suffix.png",
        File(imgPath).apply { this.mkdirs() }
    )

    File(path).copyTo(img, true)

    return Image(img.absolutePath)
}

fun Image.saveImgAndGetPath(): String {
    val img = File.createTempFile(
        "img_",
        "_background.png",
        File(imgPath).apply { this.mkdirs() }
    )

    File(path).copyTo(img, true)

    return img.absolutePath
}