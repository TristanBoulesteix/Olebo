package fr.olebo.domain.tests.models

import fr.olebo.domain.models.ElementSize
import kotlin.test.Test
import kotlin.test.assertEquals

class ElementSizeTests {
    @Test
    fun `get default value`() {
        assertEquals(ElementSize.S, ElementSize.DEFAULT)
    }
}