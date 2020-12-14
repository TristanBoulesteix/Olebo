package jdr.exia.localization

import java.util.*
import kotlin.reflect.KProperty0

/**
 * Parent class of Bundles which contains translations of all the Strings of Olebo. They are retrieved with the get operator.
 */
abstract class Strings : ListResourceBundle() {
    /**
     * The invoke() method of the companion object need to be called in order to initialize the right locale to use
     */
    companion object {
        val availableLocales = listOf(Locale.ENGLISH, Locale.FRENCH)

        private val defaultLocale = availableLocales[0]

        private var localeHandler = LocaleHandler(this::defaultLocale)

        /**
         * To set a default locale, this method need to be called by the main module
         */
        operator fun invoke(kPropertyLocale: KProperty0<Locale>) {
            localeHandler = LocaleHandler(kPropertyLocale)
        }

        private val langBundle
            get() = ResourceBundle.getBundle(
                StringsBundle::class.java.canonicalName,
                localeHandler.activeLanguage,
                Control.getNoFallbackControl(Control.FORMAT_DEFAULT)
            )

        operator fun get(key: String, state: StringStates = StringStates.CAPITALIZE): String = try {
            langBundle.getString(key)
        } catch (e: Exception) {
            key
        }.let {
            when (state) {
                StringStates.CAPITALIZE -> it.capitalize(localeHandler.activeLanguage)
                StringStates.NORMAL -> it
            }
        }
    }

    protected abstract val contents: Map<String, String>

    override fun getContents() = contents.map { it.toPair().toList().toTypedArray() }.toTypedArray()

    internal class LocaleHandler(kPropertyLocale: KProperty0<Locale>) {
        val activeLanguage by kPropertyLocale
    }
}