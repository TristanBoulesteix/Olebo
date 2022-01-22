package fr.olebo.sharescene

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.channels.Channel
import java.io.Closeable

actual class ShareSceneViewModel : Closeable {
    val messages = Channel<Message>()

    var connectedPlayers by mutableStateOf(listOf<Player>())

    val numberOfConnectedPlayers by derivedStateOf { connectedPlayers.size }

    override fun close() {
        messages.close()
    }
}