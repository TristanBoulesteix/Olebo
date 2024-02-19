package fr.olebo.persistence.services

import org.junit.jupiter.api.assertDoesNotThrow
import org.kodein.di.DI
import kotlin.test.BeforeTest
import kotlin.test.Test

internal class DatabaseServiceTests {
    private lateinit var di: DI

    private lateinit var databaseService: DatabaseService

    @BeforeTest
    fun initialize() {
        di = DI {

        }

        databaseService = DatabaseService(di)
    }

    @Test
    fun `get database connection`() {
        assertDoesNotThrow { databaseService.database }
    }
}