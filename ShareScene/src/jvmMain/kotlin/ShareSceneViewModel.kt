package fr.olebo.sharescene

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.channels.Channel
import java.io.Closeable

actual class ShareSceneViewModel : Closeable {
    val messages = Channel<Message>()

    var numberOfConnectedUser by mutableStateOf(0)

    override fun close() {
        println("close")
        messages.close()
    }
}