package jdr.exia.model.tools

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.imageFromResource
import java.io.File
import org.jetbrains.skija.Image as SkijaImage

//TODO: Replace with value class when Kotlin 1.5 will be available (if Jetpack compose is compatible)
inline class Image(val path: String) {
    companion object {
        val unspecified = Image("")
    }

    fun isValid() = !isUnspecified() && File(path).let { it.exists() && it.isFile }

    fun isUnspecified() = path.isBlank()
}

fun imageFromIconRes(name: String) = imageFromResource("icons/$name.png")

fun imageFromFile(file: File): ImageBitmap {
    return SkijaImage.makeFromEncoded(file.readBytes()).asImageBitmap()
}