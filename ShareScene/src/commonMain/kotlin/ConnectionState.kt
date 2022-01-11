package fr.olebo.sharescene

sealed interface ConnectionState

sealed interface Disconnected : ConnectionState {
    companion object : Disconnected

    object ConnectionFailed : Disconnected
}

object Login : ConnectionState

class Connected(val manager: ShareSceneManager) : ConnectionState {
    val shareSceneViewModel by manager::viewModel
}