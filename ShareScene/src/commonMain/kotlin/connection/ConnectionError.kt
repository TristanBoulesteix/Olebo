package fr.olebo.sharescene.connection

sealed interface ConnectionError {
    data object Canceled : ConnectionError
    data object WrongVersion : ConnectionError
    data object ConnectionFailed : ConnectionError
    class ServerError(val message: String?) : ConnectionError
}