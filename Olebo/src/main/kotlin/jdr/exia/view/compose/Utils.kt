package jdr.exia.view.compose

import androidx.compose.ui.graphics.imageFromResource

fun imageFromIcon(name: String) = imageFromResource("icons/$name.png")

typealias DefaultFunction = () -> Unit