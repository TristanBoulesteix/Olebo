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

        operator fun get(key: String, state: StringStates = StringStates.CAPITALIZE, vararg args: Any?): String
    }
}

operator fun StringLocale.get(key: String, vararg args: Any?) = StringLocale.get(key, StringStates.CAPITALIZE, *args)

/**
 * To set a default locale, this method need to be called by the main module
 */
inline operator fun StringLocale.Companion.invoke(getLanguage: () -> Language) {
    activeLanguage = getLanguage()
}