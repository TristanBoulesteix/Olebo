package jdr.exia.model.type

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.useResource
import jdr.exia.model.tools.toPath
import jdr.exia.system.OLEBO_DIRECTORY
import java.io.File
import java.nio.file.Path
import java.util.*
import javax.imageio.ImageIO
import kotlin.io.path.*

private val imgPath = Path(OLEBO_DIRECTORY) / "img"

@Immutable
@JvmInline
value class Image(val stringPath: String) {
    companion object {
        @Stable
        val unspecified = Image("")
    }

    @Stable
    fun isUnspecified() = stringPath.isBlank()

    @Stable
    fun toBitmap() = if (isUnspecified()) imageFromIconRes("not_found", "jpg") else imageFromPath(stringPath)

    val checkedImgPath
        get() = stringPath.toPath().toCheckedImgPath()
}

@Stable
fun imageFromIconRes(name: String, format: String = "png") = useResource("icons/$name.$format", ::loadImageBitmap)

@Stable
fun imageFromPath(path: String) =
    path.toPath().toCheckedImgPath()?.inputStream()?.buffered()?.use(::loadImageBitmap)
        ?: imageFromIconRes("not_found", "jpg")

fun Image.saveImgAndGetPath(suffix: String = "background"): String {
    imgPath.createDirectories()

    val newImgPath = imgPath / "img_${UUID.randomUUID()}_$suffix.png"

    imageStreamOf(stringPath.toPath()).use { inputStream ->
        newImgPath.outputStream().use {
            val bufferedImage = ImageIO.read(inputStream)

            //bufferedImage.resize()

            inputStream.copyTo(it)
        }
    }

    return newImgPath.relativePath
}

private val Path.relativePath: String
    get() = imgPath.toUri().relativize(toUri()).path

/**
 * @return The relative [Path] of the image or null if it not exists
 */
fun Path.toCheckedImgPath(): Path? {
    val verifiedPath = if (isAbsolute) {
        if (exists()) this else imgPath / fileName
    } else {
        imgPath / this
    }

    return if (verifiedPath.exists()) verifiedPath else null
}

private fun File?.inputStreamOrNotFound() = this?.inputStream()
    ?: ::Image::class.java.classLoader.getResourceAsStream("icons/not_found.jpg")!!

fun imageStreamOf(path: Path) = path.toCheckedImgPath()?.toFile().inputStreamOrNotFound()

/*
enum class ReducedImageSize(val width: Int, val height: Int) {
    Small(), Big()
}*/
