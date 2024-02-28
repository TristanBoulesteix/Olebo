package fr.olebo.domain.tests.models

import fr.olebo.domain.models.LabelVisibility
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LabelVisibilityTests {
    @Test
    fun `check if label is visible`() {
        assertTrue(LabelVisibility.OnlyForMaster.isVisible)
        assertTrue(LabelVisibility.ForBoth.isVisible)
        assertFalse(LabelVisibility.Hidden.isVisible)
    }

    @Test
    fun `encode - decode to JSON`() {
        assertEquals("\"OnlyForMaster\"", LabelVisibility.OnlyForMaster.encode())
        assertEquals(LabelVisibility.ForBoth, LabelVisibility["\"ForBoth\""])
        assertEquals(LabelVisibility.OnlyForMaster, LabelVisibility[""])
    }
}