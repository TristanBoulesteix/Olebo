package jdr.exia.localization

expect sealed class StringLocale constructor() {
    internal abstract val contents: Map<String, String>

    /**
     * The [invoke] method of the companion object need to be called in order to initialize the right locale to use
     */
    companion object {
        @PublishedApi
        internal var activeLanguage: Language

        internal val langBundle: ResourceBundle
    }
}

operator fun StringLocale.Companion.get(key: String, state: StringStates, vararg args: Any): String = try {
    langBundle.getString(key)
} catch (e: Exception) {
    key
}.let { string ->
    when (state) {
        StringStates.CAPITALIZE -> string.replaceFirstChar { if (it.isLowerCase()) it.titleCase(activeLanguage) else it.toString() }
        StringStates.NORMAL -> string
    }.format(*args)
}

operator fun StringLocale.Companion.get(key: String, vararg args: Any) =
    StringLocale.get(key, StringStates.CAPITALIZE, *args)

/**
 * To set a default locale, this method need to be called by the main module
 */
inline operator fun StringLocale.Companion.invoke(getLanguage: () -> Language) {
    activeLanguage = getLanguage()
}

expect fun Char.titleCase(language: Language): String

internal expect fun String.format(vararg args: Any): String