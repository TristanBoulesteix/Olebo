package jdr.exia.model.tools

typealias SimpleResult = Result<Unit>

val Result.Companion.success: SimpleResult
    inline get() = success(Unit)

val Result.Companion.failure
    inline get() = failure<Nothing>(Exception())

fun Result.Companion.failure(message: String) = failure<Nothing>(IllegalStateException(message))