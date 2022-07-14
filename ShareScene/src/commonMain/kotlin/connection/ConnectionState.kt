package fr.olebo.sharescene.connection

import fr.olebo.sharescene.ShareSceneManager

sealed interface ConnectionState

sealed interface Disconnected : ConnectionState {
    companion object : Disconnected

    class ConnectionFailed(val error: ConnectionError = ConnectionError.Canceled) : Disconnected
}

object Login : ConnectionState

class Connected(val manager: ShareSceneManager) : ConnectionState {
    val shareSceneViewModel by manager::viewModel
}