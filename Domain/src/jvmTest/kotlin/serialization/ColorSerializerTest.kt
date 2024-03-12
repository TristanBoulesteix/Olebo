package fr.olebo.domain.tests.serialization

import androidx.compose.ui.graphics.Color
import fr.olebo.domain.serialization.ColorSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

internal class ColorSerializerTest {
    private lateinit var serializer: KSerializer<Color>

    @BeforeTest
    fun initialize() {
        serializer = ColorSerializer()
    }

    @Test
    fun `serialize a color`() {
        val color = Color.Blue

        val stringColor = Json.encodeToString(serializer, color)
        assertEquals("\"-16776961\"", stringColor)

        val actualColor = Json.decodeFromString(serializer, stringColor)
        assertEquals(color, actualColor)
    }
}