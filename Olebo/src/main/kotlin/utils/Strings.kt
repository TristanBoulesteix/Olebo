package utils

@Suppress("ClassName")
sealed class Strings {
    abstract val value1: String

    object fr : Strings() {
        override val value1 = ""
    }

    object en : Strings() {
        override val value1 = ""
    }

    operator fun get()
}