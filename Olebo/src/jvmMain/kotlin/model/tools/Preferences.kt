package jdr.exia.model.tools

import jdr.exia.model.dao.option.ThemeMode
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.prefs.Preferences

object Preferences {
    private val prefs: Preferences = Preferences.userNodeForPackage(javaClass)

    var themeMode: ThemeMode
        get() {
            val prefValue = prefs["theme", null] ?: return ThemeMode.Auto
            return try {
                Json.decodeFromString(prefValue)
            } catch (e: SerializationException) {
                ThemeMode.Auto
            }
        }
        set(value) = prefs.put("theme", Json.encodeToString(value))
}