package fr.olebo.domain.tests.models

import fr.olebo.domain.models.TypeElement
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TypeElementTests {
    @Test
    fun `validate custom element`() {
        assertTrue(TypeElement.Object.isCustom)
        assertTrue(TypeElement.PJ.isCustom)
        assertTrue(TypeElement.PNJ.isCustom)
        assertFalse(TypeElement.Basic.isCustom)
    }
}