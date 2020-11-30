package model.internationalisation

import model.dao.Settings
import java.util.*

abstract class Strings : ListResourceBundle() {
    companion object {
        private val langBundle
            get() = ResourceBundle.getBundle(StringsBundle::class.java.canonicalName!!, Settings.language)

        operator fun get(key: String): String = try {
            langBundle.getString(key)
        } catch (e: Exception) {
            key
        }
    }

    abstract val contents: Map<String, String>

    override fun getContents() = contents.map { it.toPair().toList().toTypedArray() }.toTypedArray()
}