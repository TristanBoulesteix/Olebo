package jdr.exia.model.dao.option

import fr.olebo.sharescene.URL
import jdr.exia.DeveloperModeManager
import jdr.exia.OLEBO_VERSION_CODE
import kotlinx.serialization.Serializable
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

    private var updateAttempts by preference("update-attempts", UpdateAttempt.none())

    val wasJustUpdated
        get() = versionUpdatedTo == OLEBO_VERSION_CODE

    init {
        if (updateAttempts.versionCode <= OLEBO_VERSION_CODE) {
            updateAttempts = UpdateAttempt.none()
        }
    }

    fun getNumberOfUpdateAttemptForVersion(version: Int) =
        updateAttempts.takeIf { it.versionCode == version }?.attemptNumber ?: 0

    fun incrementAttemptForVersion(version: Int) {
        val previousAttempt = updateAttempts

        updateAttempts = if (previousAttempt.versionCode == version) {
            previousAttempt.copy(attemptNumber = updateAttempts.attemptNumber + 1)
        } else {
            UpdateAttempt(version)
        }
    }

    @Serializable
    private data class UpdateAttempt(val versionCode: Int, val attemptNumber: Int = 0) {
        companion object {
            fun none() = UpdateAttempt(-1)
        }
    }

    var oleboUrl by preference("url", URL("https://olebo.fr"), isDeveloperSetting = true)

    private inline fun <reified T> preference(key: String, defaultValue: T, isDeveloperSetting: Boolean = false) =
        object : ReadWriteProperty<Preferences, T> {
            override fun getValue(thisRef: Preferences, property: KProperty<*>): T {
                if(isDeveloperSetting && !DeveloperModeManager.isCurrentlyEnabled) {
                    return defaultValue
                }

                val prefValue = prefs[key, null] ?: return defaultValue
                return try {
                    Json.decodeFromString(prefValue)
                } catch (e: SerializationException) {
                    defaultValue
                }
            }

            override fun setValue(thisRef: Preferences, property: KProperty<*>, value: T) =
                prefs.put(key, Json.encodeToString(value))
        }
}