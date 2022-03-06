package jdr.exia.localization

expect class Locale

expect value class Language(@Suppress("unused") val locale: Locale /*Suppress unused. This warning is a bug from the Kotlin Multiplatform plugin.*/) {
    companion object {
        internal val english: Language
        internal val french: Language
    }
}

expect val Language.languageCode: String