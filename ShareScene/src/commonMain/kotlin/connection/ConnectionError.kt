package fr.olebo.sharescene.connection

sealed interface ConnectionError {
    object Canceled : ConnectionError
    object WrongVersion : ConnectionError
    object ConnectionFailed : ConnectionError
    class ServerError(val message: String?) : ConnectionError
}