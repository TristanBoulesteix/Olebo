package fr.olebo.domain.tests.serialization

import androidx.compose.ui.graphics.Color
import fr.olebo.domain.serialization.ColorSerializer
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class ColorSerializerTest {
    @Test
    fun `serialize a color`() {
        val color = Color.Blue

        val stringColor = Json.encodeToString(ColorSerializer(), color)
        assertEquals("\"-16776961\"", stringColor)

        val actualColor = Json.decodeFromString(ColorSerializer(), stringColor)
        assertEquals(color, actualColor)
    }
}