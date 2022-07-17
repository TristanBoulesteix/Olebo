package jdr.exia.model.dao

import jdr.exia.OLEBO_VERSION_CODE
import jdr.exia.model.dao.option.ThemeMode
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty
import java.util.prefs.Preferences as JavaPreferences

object Preferences {
    private val prefs: JavaPreferences = JavaPreferences.userNodeForPackage(javaClass)

    var themeMode: ThemeMode by preference("theme", ThemeMode.Auto)

    var versionUpdatedTo by preference("last-update", -1)

    val wasJustUpdated
        get() = versionUpdatedTo == OLEBO_VERSION_CODE

    private inline fun <reified T> preference(key: String, defaultValue: T) =
        object : ReadWriteProperty<Preferences, T> {
            override fun getValue(thisRef: Preferences, property: KProperty<*>): T {
                val prefValue = prefs[key, null] ?: return defaultValue
                return try {
                    Json.decodeFromString(prefValue)
                } catch (e: SerializationException) {
                    defaultValue
                }
            }

            override fun setValue(thisRef: Preferences, property: KProperty<*>, value: T) = prefs.put(key, Json.encodeToString(value))
        }
}