package jdr.exia.service

sealed interface ConnectionState

object Disconnected : ConnectionState

object ConnectionFailed: ConnectionState

object Login : ConnectionState

class Connected(val manager: ShareSceneManager) : ConnectionState