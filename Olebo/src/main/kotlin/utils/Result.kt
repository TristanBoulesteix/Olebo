package utils

sealed class Result<T> {
    abstract val value: T?

    data class Success<T>(override val value: T? = null) : Result<T>()

    data class Failed<T>(override val value: T? = null) : Result<T>()
}