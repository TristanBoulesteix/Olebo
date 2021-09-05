package jdr.exia.model.tools

import jdr.exia.localization.ST_UNKNOWN_ERROR
import jdr.exia.localization.StringDelegate

sealed interface Result {
    object Success : Result

    sealed class Failure : Result {
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
