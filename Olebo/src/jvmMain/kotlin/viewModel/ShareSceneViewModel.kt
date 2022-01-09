package jdr.exia.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import fr.olebo.sharescene.ConnectionState
import fr.olebo.sharescene.Disconnected
import fr.olebo.sharescene.Message
import kotlinx.coroutines.channels.Channel
import java.io.Closeable

class ShareSceneViewModel(val connectionState: ConnectionState = Disconnected) : Closeable {
    val messages = Channel<Message>()

    var numberOfConnectedUser by mutableStateOf(0)

    override fun close() {
        messages.close()
    }
}