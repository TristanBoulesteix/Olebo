package model.dao.internationalisation

import model.dao.Settings
import java.util.*

abstract class Strings : ListResourceBundle() {
    companion object {
        private val langBundle
            get() = ResourceBundle.getBundle(StringsBundle::class.java.canonicalName!!, Settings.language)

        val availableLocales = mapOf<String, Locale>("English" to Locale.ENGLISH, "Français" to Locale.FRENCH)
            get() = field.let { map ->
                Locale.getDefault().let { locale ->
                    if (map.map { it.value.language }.contains(locale.language)) map
                    else mapOf(locale.getDisplayLanguage(locale) to locale) + map
                }
            }

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