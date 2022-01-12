package fr.olebo.sharescene

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import io.ktor.utils.io.core.*

actual class ShareSceneViewModel : Closeable {
    var background by mutableStateOf(Base64Image(""))

    val tokens = mutableStateListOf<Token>()

    override fun close() = Unit
}