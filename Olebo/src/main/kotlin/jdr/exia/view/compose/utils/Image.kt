package jdr.exia.view.compose.utils

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.imageFromResource
import org.jetbrains.skija.Image
import java.io.File

fun imageFromIcon(name: String) = imageFromResource("icons/$name.png")

fun imageFromFile(file: File): ImageBitmap {
    return Image.makeFromEncoded(file.readBytes()).asImageBitmap()
}