package fr.olebo.memory.services

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import java.util.prefs.Preferences

/**
 * Service for storing and retrieving values in preferences.
 */
internal interface PreferenceService {
    /**
     * Retrieves a value from preferences.
     *
     * @param T The type of the value.
     * @param serializer The serializer for the type.
     * @param key The key for the preference.
     * @return The value associated with the key, or null if not found.
     */
    operator fun <T> get(serializer: KSerializer<T?>, key: String): T?

    /**
     * Stores a value in preferences.
     *
     * @param T The type of the value.
     * @param serializer The serializer for the type.
     * @param key The key for the preference.
     * @param value The value to store.
     */
    operator fun <T> set(serializer: KSerializer<T>, key: String, value: T)
}

/**
 * Implementation of the PreferenceService interface using Java Preferences API.
 *
 * @property preferences The Preferences instance to use for storing and retrieving values.
 */
internal class PreferenceServiceImpl(private val preferences: Preferences) : PreferenceService {
    /**
     * Retrieves a value from preferences.
     *
     * @param T The type of the value.
     * @param serializer The serializer for the type.
     * @param key The key for the preference.
     * @return The value associated with the key, or null if not found.
     */
    override fun <T> get(serializer: KSerializer<T?>, key: String): T? {
        val value = preferences.get(key, null) ?: return null
        return Json.decodeFromString(serializer, value)
    }

    /**
     * Stores a value in preferences.
     *
     * @param T The type of the value.
     * @param serializer The serializer for the type.
     * @param key The key for the preference.
     * @param value The value to store.
     */
    override fun <T> set(serializer: KSerializer<T>, key: String, value: T) =
        preferences.put(key, Json.encodeToString(serializer, value))
}

/**
 * Retrieves a value from preferences.
 *
 * @see PreferenceService.get
 *
 * @param T The type of the value.
 * @param key The key for the preference.
 * @return The value associated with the key, or null if not found.
 */
internal inline operator fun <reified T> PreferenceService.get(key: String): T? = get(serializer<T?>(), key)

/**
 * Stores a value in preferences.
 *
 * @see PreferenceService.set
 *
 * @param T The type of the value.
 * @param key The key for the preference.
 * @param value The value to store.
 */
internal inline operator fun <reified T> PreferenceService.set(key: String, value: T) = set(serializer(), key, value)