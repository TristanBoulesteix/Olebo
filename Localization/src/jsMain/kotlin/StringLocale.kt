package jdr.exia.localization

actual sealed class StringLocale {
    /**
     * The [invoke] method of the companion object need to be called in order to initialize the right locale to use
     */
    actual companion object {
        @PublishedApi
        internal actual var activeLanguage = defaultLocale

        internal actual val langBundle: ResourceBundle
            get() = ResourceBundle(activeLanguage)
    }

    internal actual abstract val contents: Map<String, String>
}

actual fun Char.titleCase(language: Language) = titlecase()

actual fun String.format(vararg args: Any?) = this // TODO