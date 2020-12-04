package model.dao.internationalisation

import model.dao.Settings
import java.util.*

abstract class Strings : ListResourceBundle() {
    companion object {
        val availableLocales = listOf(Locale.ENGLISH, Locale.FRENCH)

        private val langBundle
            get() = ResourceBundle.getBundle(
                StringsBundle::class.java.canonicalName,
                Settings.language,
                Control.getNoFallbackControl(Control.FORMAT_DEFAULT)
            )

        operator fun get(key: String, state: StringStates = StringStates.CAPITALIZE): String = try {
            langBundle.getString(key)
        } catch (e: Exception) {
            key
        }.let {
            when (state) {
                StringStates.CAPITALIZE -> it.capitalize(Settings.language)
                StringStates.NORMAL -> it
            }
        }
    }

    protected abstract val contents: Map<String, String>

    override fun getContents() = contents.map { it.toPair().toList().toTypedArray() }.toTypedArray()
}