package fr.olebo.utils

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

sealed class Result<SuccessValue, ErrorValue> {
    @PublishedApi
    internal class Success<SuccessValue, ErrorValue>(val value: SuccessValue) : Result<SuccessValue, ErrorValue>()

    @PublishedApi
    internal class Exception<SuccessValue, ErrorValue>(val error: Throwable) : Result<SuccessValue, ErrorValue>()

    @PublishedApi
    internal class Failure<SuccessValue, ErrorValue>(val error: ErrorValue) : Result<SuccessValue, ErrorValue>()

    companion object {
        fun <SuccessValue, ErrorValue> success(value: SuccessValue): Result<SuccessValue, ErrorValue> = Success(value)
        fun <SuccessValue, ErrorValue> exception(error: Throwable): Result<SuccessValue, ErrorValue> = Exception(error)
        fun <SuccessValue, ErrorValue> failure(error: ErrorValue): Result<SuccessValue, ErrorValue> = Failure(error)
    }
}

fun <SuccessValue, ErrorValue> Result<SuccessValue, ErrorValue>.getOrNull() =
    if (this is Result.Success) value else null

@OptIn(ExperimentalContracts::class)
inline fun <SuccessValue, ErrorValue> Result<SuccessValue, ErrorValue>.onSuccess(action: (value: SuccessValue) -> Unit): Result<SuccessValue, ErrorValue> {
    contract {
        callsInPlace(action, InvocationKind.AT_MOST_ONCE)
    }

    if (this is Result.Success) {
        action(value)
    }

    return this
}

@OptIn(ExperimentalContracts::class)
inline fun <SuccessValue, ErrorValue> Result<SuccessValue, ErrorValue>.onFailure(action: (errorValue: ErrorValue) -> Unit): Result<SuccessValue, ErrorValue> {
    contract {
        callsInPlace(action, InvocationKind.AT_MOST_ONCE)
    }

    if (this is Result.Failure) {
        action(error)
    }

    return this
}

@OptIn(ExperimentalContracts::class)
inline fun <SuccessValue, ErrorValue> Result<SuccessValue, ErrorValue>.onThrow(action: (throwable: Throwable) -> Unit): Result<SuccessValue, ErrorValue> {
    contract {
        callsInPlace(action, InvocationKind.AT_MOST_ONCE)
    }

    if (this is Result.Exception) {
        action(error)
    }

    return this
}

@OptIn(ExperimentalContracts::class)
inline fun <SuccessValue, ErrorValue> Result<SuccessValue, ErrorValue>.onNotSuccess(action: (failureValue: ErrorValue?, throwable: Throwable?) -> Unit): Result<SuccessValue, ErrorValue> {
    contract {
        callsInPlace(action, InvocationKind.AT_MOST_ONCE)
    }

    var failureValue: ErrorValue? = null
    var exception: Throwable? = null

    onFailure { failureValue = it }
    onThrow { exception = it }

    action(failureValue, exception)

    return this
}

val Result<*, *>.isSuccess
    inline get() = this is Result.Success

val Result<*, *>.isFailure
    inline get() = !isSuccess