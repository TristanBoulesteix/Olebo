package jdr.exia.localization

import java.io.InputStream
import java.util.*

/**
 * Parent class of Bundles which contains translations of all the StringLocale of Olebo. They are retrieved with the get operator.
 */
actual sealed class StringLocale : ListResourceBundle() {
    internal actual abstract val contents: Map<String, String>

    override fun getContents() = contents.map { it.toPair().toList().toTypedArray() }.toTypedArray()

    /**
     * The [invoke] method of the companion object need to be called in order to initialize the right locale to use
     */
    actual companion object {
        @PublishedApi
        internal actual var activeLanguage = defaultLocale

        internal actual val langBundle
            get() = ResourceBundle.getBundle(
                StringLocaleBundle::class.java.canonicalName,
                activeLanguage.locale,
                Control.getNoFallbackControl(Control.FORMAT_DEFAULT)
            )

        actual operator fun get(key: String, state: StringStates, vararg args: Any?): String = try {
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

            return locales.firstNotNullOfOrNull {
                val bundleName: String = control.toBundleName(resourceName, it)

                classLoader.getResourceAsStream(control.toResourceName(bundleName, extension))
            }
        }
    }
}