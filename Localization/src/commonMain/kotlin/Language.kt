package jdr.exia.localization

expect class Locale(code: String)

expect value class Language(val locale: Locale) {
    companion object {
        internal val english: Language
        internal val french: Language
    }
}

fun Language(locale: String) = Language(Locale(locale))

expect val Language.languageCode: String