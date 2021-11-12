package jdr.exia.model.type

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.useResource
import jdr.exia.system.OLEBO_DIRECTORY
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import kotlin.io.path.*

private val imgPath = Path(OLEBO_DIRECTORY) / "img"

@Immutable
@JvmInline
value class Image(val path: String) {
    companion object {
        @Stable
        val unspecified = Image("")
    }

    fun isUnspecified() = path.isBlank()

    @Stable
    fun toBitmap() = imageFromPath(path)

    val checkedImgPath
        get() = path.toImgPath().checkedImgPath()
}

@Stable
fun imageFromIconRes(name: String, format: String = "png") = useResource("icons/$name.$format", ::loadImageBitmap)

@Stable
fun imageFromPath(path: String) =
    path.toImgPath().checkedImgPath()?.inputStream()?.buffered()?.use(::loadImageBitmap)
        ?: imageFromIconRes("not_found", "jpg")

fun Image.saveImgAndGetPath(suffix: String = "background"): String {
    imgPath.createDirectories()

    val newImgPath = imgPath / "img_${UUID.randomUUID()}_$suffix.png"

    path.toImgPath().checkedImgPath()?.toFile().inputStreamOrNotFound().use { inputStream ->
        newImgPath.outputStream().use {
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
fun Path.checkedImgPath(): Path? {
    val verifiedPath = if (isAbsolute) {
        if (exists()) this else {
            imgPath / fileName
        }
    } else {
        imgPath / this
    }

    return if (verifiedPath.exists()) verifiedPath else null
}

fun String.toImgPath(): Path = Paths.get(this)

fun File?.inputStreamOrNotFound() = this?.inputStream()
    ?: ::Image::class.java.classLoader.getResourceAsStream("icons/not_found.jpg")!!