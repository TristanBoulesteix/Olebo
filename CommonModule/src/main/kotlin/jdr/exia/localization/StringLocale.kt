package jdr.exia.localization

import jdr.exia.defaultLocale
import java.util.*
import kotlin.reflect.KProperty0

/**
 * Parent class of Bundles which contains translations of all the StringLocale of Olebo. They are retrieved with the get operator.
 */
abstract class StringLocale : ListResourceBundle() {
    /**
     * The invoke() method of the companion object need to be called in order to initialize the right locale to use
     */
    companion object {
        private var localeHandler = LocaleHandler(::defaultLocale)

        /**
         * To set a default locale, this method need to be called by the main module
         */
        operator fun invoke(kPropertyLocale: KProperty0<Locale>) {
            localeHandler = LocaleHandler(kPropertyLocale)
        }

        private val langBundle
            get() = ResourceBundle.getBundle(
                StringLocaleBundle::class.java.canonicalName,
                localeHandler.activeLanguage,
                Control.getNoFallbackControl(Control.FORMAT_DEFAULT)
            )

        operator fun get(key: String, state: StringStates = StringStates.CAPITALIZE, vararg args: Any?): String = try {
            langBundle.getString(key)
        } catch (e: Exception) {
            key
        }.let { string ->
            when (state) {
                StringStates.CAPITALIZE -> string.replaceFirstChar { if (it.isLowerCase()) it.titlecase(localeHandler.activeLanguage) else it.toString() }
                StringStates.NORMAL -> string
            }.format(*args)
        }

        operator fun get(key: String, vararg args: Any?) = get(key, StringStates.CAPITALIZE, *args)
    }

    protected abstract val contents: Map<String, String>

    override fun getContents() = contents.map { it.toPair().toList().toTypedArray() }.toTypedArray()

    internal class LocaleHandler(kPropertyLocale: KProperty0<Locale>) {
        val activeLanguage by kPropertyLocale
    }
}