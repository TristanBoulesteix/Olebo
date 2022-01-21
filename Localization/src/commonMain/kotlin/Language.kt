package jdr.exia.localization

expect class Locale

@Suppress("unused") // Suppress unused. This warning is a bug from the Kotlin Multiplatform plugin.
expect value class Language(val locale: Locale) {
    companion object {
        internal val english: Language
        internal val french: Language
    }
}

expect val Language.languageCode: String