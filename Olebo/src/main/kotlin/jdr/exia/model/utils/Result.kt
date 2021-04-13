package jdr.exia.model.utils

sealed class Result {
    object Success : Result()

    class Failure(val message: String) : Result()
}
