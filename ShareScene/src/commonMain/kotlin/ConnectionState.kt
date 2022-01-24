package fr.olebo.sharescene

sealed interface ConnectionState

sealed interface Disconnected : ConnectionState {
    companion object : Disconnected

    class ConnectionFailed(val error: Throwable? = null) : Disconnected
}

object Login : ConnectionState

class Connected(val manager: ShareSceneManager) : ConnectionState {
    val shareSceneViewModel by manager::viewModel
}