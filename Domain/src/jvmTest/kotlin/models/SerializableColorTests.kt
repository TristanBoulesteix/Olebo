package fr.olebo.domain.tests.models

import androidx.compose.ui.graphics.Color
import fr.olebo.domain.models.SerializableColor
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

class SerializableColorTests {
    @Test
    fun `check all serializable color values`() {
        fun checkColorsFor(sc: SerializableColor, expectedColor1: Color, expectedColor2: Color = expectedColor1) {
            val (actualColor1, actualColor2) = sc
            assertEquals(expectedColor1, actualColor1)
            assertEquals(expectedColor2, actualColor2)
        }

        checkColorsFor(SerializableColor.BlackWhite, Color.Black, Color.White)
        checkColorsFor(SerializableColor.WhiteBlack, Color.White, Color.Black)
        checkColorsFor(SerializableColor.BLACK, Color.Black)
        checkColorsFor(SerializableColor.PURPLE, Color(168, 50, 143))
        checkColorsFor(SerializableColor.YELLOW, Color.Yellow)
        checkColorsFor(SerializableColor.RED, Color.Red)

        val expectedColor = Color.DarkGray
        checkColorsFor(SerializableColor.Custom(expectedColor), expectedColor)

        checkColorsFor(SerializableColor.Unspecified, Color.Unspecified)
    }

    @Test
    fun `check that all serializable colors are serializable`() {
        SerializableColor::class.sealedSubclasses.forEach {
            it.objectInstance?.let { sc ->
                try {
                    Json.encodeToString(sc)
                } catch (e: Exception) {
                    assertFails { throw e }
                }
            }
        }
    }

    @Test
    fun `decode color`() {
        val key = """{"type": "fr.olebo.domain.models.SerializableColor.RED"}"""
        val color = SerializableColor[key]
        assertEquals(SerializableColor.RED, color, "Test existing color")

        assertEquals(SerializableColor.YELLOW, SerializableColor["fdh"], "Test absent color and default color")
    }

    @Test
    fun `encode color`() {
        val encodedString = SerializableColor.PURPLE.encode()

        assertEquals("""{"type":"fr.olebo.domain.models.SerializableColor.PURPLE"}""", encodedString)
    }
}