package jdr.exia.model.type

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.useResource
import jdr.exia.system.OLEBO_DIRECTORY
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.Path
import kotlin.io.path.div
import kotlin.io.path.exists

private val imgPath = Path(OLEBO_DIRECTORY) / "img"

@Immutable
@JvmInline
value class Image(val path: String) {
    companion object {
        @Stable
        val unspecified = Image("")
    }

    fun isUnspecified() = path.isBlank()

    fun toBitmap() = imageFromPath(path)

    val checkedImgPath
        get() = path.toImgPath().checkedImgPath()
}

@Stable
fun imageFromIconRes(name: String, format: String = "png") = useResource("icons/$name.$format", ::loadImageBitmap)

@Stable
fun imageFromPath(path: String) =
    path.toImgPath().checkedImgPath()?.toFile()?.inputStream()?.buffered()?.use(::loadImageBitmap)
        ?: imageFromIconRes("not_found", "jpg")

fun Image.saveImgAndGetPath(suffix: String = "background"): String {
    val img = File.createTempFile(
        "img_",
        "_$suffix.png",
        imgPath.toFile().apply { this.mkdirs() }
    )

    path.toImgPath().checkedImgPath()?.toFile().inputStreamOrNotFound().use { inputStream ->
        img.outputStream().use {
            inputStream.copyTo(it)
        }
    }

    return img.relativePath
}

private val File.relativePath: String
    get() = imgPath.toUri().relativize(toURI()).path

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