package jdr.exia.localization

import kotlinx.browser.window

actual typealias Locale = String

actual value class Language(val locale: Locale) {
    actual companion object {
        internal actual val english = Language("en")
        internal actual val french = Language("fr")
    }
}

actual val Language.languageCode: String
    get() = locale

fun getBrowserLanguage(): Language {
    val navigator = window.navigator

    val availableLanguageCode = availableLocales.map(Language::languageCode)

    val browserLocales = navigator.languages.takeIf { it != undefined } ?: arrayOf(navigator.language)

    if (browserLocales == undefined)
        return Language.english

    val acceptedLanguages =
        browserLocales.mapNotNull { it.trim().split("/-|_/".toRegex()).firstOrNull() }

    acceptedLanguages.firstOrNull { it in availableLanguageCode }?.let {
        if (it.equals(Language.french.languageCode, ignoreCase = true))
            return Language.french
    }

    return Language.english
}