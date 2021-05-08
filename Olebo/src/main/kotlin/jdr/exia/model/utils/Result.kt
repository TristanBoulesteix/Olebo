package jdr.exia.model.utils

import jdr.exia.localization.ST_UNKNOWN_ERROR
import jdr.exia.localization.StringDelegate

sealed class Result {
    object Success : Result()

    sealed class Failure : Result() {
        companion object : Failure() {
            override val causeUnknown = true

            override val message by StringDelegate(ST_UNKNOWN_ERROR)

            operator fun invoke(message: String): Failure = DetailledFailure(message)
        }

        abstract val message: String

        abstract val causeUnknown: Boolean

        private class DetailledFailure(override val message: String) : Failure() {
            override val causeUnknown = false
        }
    }
}
