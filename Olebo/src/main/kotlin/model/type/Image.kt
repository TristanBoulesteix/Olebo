package jdr.exia.model.type

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.useResource
import jdr.exia.system.OLEBO_DIRECTORY
import java.io.File
import org.jetbrains.skija.Image as SkijaImage

private val imgPath = OLEBO_DIRECTORY + "img${File.separator}"

@Immutable
@JvmInline
value class Image(val path: String) {
    companion object {
        @Stable
        val unspecified = Image("")
    }

    fun isValid() = !isUnspecified() && File(path).let { it.exists() && it.isFile }

    fun isUnspecified() = path.isBlank()

    fun toBitmap() = imageFromFile(File(path))
}

fun imageFromIconRes(name: String) = useResource("icons/$name.png", ::loadImageBitmap)

fun imageFromFile(file: File) = SkijaImage.makeFromEncoded(file.readBytes()).asImageBitmap()

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