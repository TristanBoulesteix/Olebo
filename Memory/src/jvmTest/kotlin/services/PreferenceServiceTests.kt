package fr.olebo.memory.tests.services

import fr.olebo.memory.services.PreferenceService
import fr.olebo.memory.services.PreferenceServiceImpl
import fr.olebo.memory.services.get
import fr.olebo.memory.services.set
import fr.olebo.memory.tests.model.PreferencesMock
import junit.framework.TestCase.assertEquals
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNull

internal class PreferenceServiceImplTest {
    private lateinit var preferences: PreferencesMock
    private lateinit var preferenceService: PreferenceService

    @BeforeTest
    fun initialize() {
        preferences = PreferencesMock()
        preferenceService = PreferenceServiceImpl(preferences)
    }

    @Test
    fun `get string value from preferences`() {
        // Given
        val key = "stringKey"
        val expected = "expected"

        // When
        val result: String? = preferenceService[key]

        // Then
        assertEquals(expected, result)
    }

    @Test
    fun `get int value from preferences`() {
        // Given
        val key = "intKey"
        val expected = 42

        // When
        val result: Int? = preferenceService[key]

        // Then
        assertEquals(expected, result)
    }

    @Test
    fun `get unknown value from preferences`() {
        // Given
        val key = "unknownKey"

        // When
        val result: String? = preferenceService[key]

        // Then
        assertNull(result)
    }

    @Test
    fun `set string value in preferences`() {
        // Given
        @Serializable
        data class Data(val value: String)

        val key = "newData"
        val value = Data("new value")

        // When
        preferenceService[key] = value

        // Then
        assertEquals(value, Json.decodeFromString<Data>(preferences.get(key, null)!!))
    }
}