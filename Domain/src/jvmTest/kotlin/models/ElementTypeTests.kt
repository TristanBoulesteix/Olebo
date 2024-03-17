package fr.olebo.domain.tests.models

import fr.olebo.domain.models.ElementType
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ElementTypeTests {
    @Test
    fun `validate custom element`() {
        assertTrue(ElementType.Object.isCustom)
        assertTrue(ElementType.PJ.isCustom)
        assertTrue(ElementType.PNJ.isCustom)
        assertFalse(ElementType.Basic.isCustom)
    }
}