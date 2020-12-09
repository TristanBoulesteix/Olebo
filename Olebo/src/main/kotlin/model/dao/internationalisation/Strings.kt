package model.dao.internationalisation

import model.dao.option.Settings
import java.util.*

/**
 * Parent class of Bundles which contains translations of all the Strings of Olebo. They are retrieved with the get operator.
 */
abstract class Strings : ListResourceBundle() {
    companion object {
        val availableLocales = listOf(Locale.ENGLISH, Locale.FRENCH)

        private val langBundle
            get() = ResourceBundle.getBundle(
                StringsBundle::class.java.canonicalName,
                Settings.activeLanguage,
                Control.getNoFallbackControl(Control.FORMAT_DEFAULT)
            )

        operator fun get(key: String, state: StringStates = StringStates.CAPITALIZE): String = try {
            langBundle.getString(key)
        } catch (e: Exception) {
            key
        }.let {
            when (state) {
                StringStates.CAPITALIZE -> it.capitalize(Settings.activeLanguage)
                StringStates.NORMAL -> it
            }
        }
    }

    protected abstract val contents: Map<String, String>

    override fun getContents() = contents.map { it.toPair().toList().toTypedArray() }.toTypedArray()
}