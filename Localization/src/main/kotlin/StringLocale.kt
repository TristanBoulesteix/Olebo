package jdr.exia.localization

import java.io.InputStream
import java.util.*

/**
 * Parent class of Bundles which contains translations of all the StringLocale of Olebo. They are retrieved with the get operator.
 */
sealed class StringLocale : ListResourceBundle() {
    /**
     * The [invoke] method of the companion object need to be called in order to initialize the right locale to use
     */
    companion object {
        @PublishedApi
        internal var activeLanguage = defaultLocale

        /**
         * To set a default locale, this method need to be called by the main module
         */
        inline operator fun invoke(getLanguage: () -> Language) {
            activeLanguage = getLanguage()
        }

        private val langBundle
            get() = ResourceBundle.getBundle(
                StringLocaleBundle::class.java.canonicalName,
                activeLanguage.locale,
                Control.getNoFallbackControl(Control.FORMAT_DEFAULT)
            )

        operator fun get(key: String, state: StringStates = StringStates.CAPITALIZE, vararg args: Any?): String = try {
            langBundle.getString(key)
        } catch (e: Exception) {
            key
        }.let { string ->
            when (state) {
                StringStates.CAPITALIZE -> string.replaceFirstChar { if (it.isLowerCase()) it.titlecase(activeLanguage.locale) else it.toString() }
                StringStates.NORMAL -> string
            }.format(*args)
        }

        operator fun get(key: String, vararg args: Any?) = get(key, StringStates.CAPITALIZE, *args)

        /**
         * @param resourceName The name of the resource or its path
         * @param extension The extension of the resource (For example "txt")
         * @param classLoader The [ClassLoader] to load the resource
         *
         * @return The [InputStream] of a resource according to the current active [Language].
         */
        fun getLocalizedResource(resourceName: String, extension: String, classLoader: ClassLoader): InputStream? {
            val control: Control = Control.getControl(Control.FORMAT_DEFAULT)
            val locales: List<Locale> = control.getCandidateLocales(resourceName, activeLanguage.locale)

            return locales.mapNotNull {
                val bundleName: String = control.toBundleName(resourceName, it)

                classLoader.getResourceAsStream(control.toResourceName(bundleName, extension))
            }.firstOrNull()
        }
    }

    protected abstract val contents: Map<String, String>

    override fun getContents() = contents.map { it.toPair().toList().toTypedArray() }.toTypedArray()
}