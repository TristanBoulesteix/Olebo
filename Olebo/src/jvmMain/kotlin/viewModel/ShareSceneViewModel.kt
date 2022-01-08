package jdr.exia.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import fr.olebo.sharescene.ConnectionState
import fr.olebo.sharescene.Disconnected

class ShareSceneViewModel {
    private var _connectionState: ConnectionState by mutableStateOf(Disconnected)

    var connectionState
        get() = _connectionState
        set(value) {
            if (value is Disconnected) {
                numberOfConnectedUser = 0
            }
            _connectionState = value
        }

    var numberOfConnectedUser by mutableStateOf(0)
}