package fr.olebo.persistence

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.assertNotNull

class DatabaseTest {
    @Test
    fun `generate database`() {
        val database = assertDoesNotThrow { initializeDatabase() }

        assertNotNull(database)
    }
}