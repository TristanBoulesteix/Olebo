package jdr.exia.localization

expect class Locale

expect value class Language(val locale: Locale) {
    companion object {
        internal val english: Language
        internal val french: Language
    }
}

expect val Language.languageCode: String