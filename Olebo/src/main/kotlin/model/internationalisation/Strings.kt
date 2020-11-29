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

        // -- Keys
        const val STR_VERSION = "version"
        const val STR_FILES = "files"
        const val STR_TAKE_SCREENSHOT = "take_screenshot"
        const val STR_FILE_ALREADY_EXIST = "file_already_exist"
        const val STR_SAVE_AS = "save_as"
    }

    abstract val contents: Map<String, String>

    override fun getContents() = contents.map { it.toPair().toList().toTypedArray() }.toTypedArray()
}