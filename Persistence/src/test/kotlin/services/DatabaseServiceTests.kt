package fr.olebo.persistence.tests.services

import fr.olebo.persistence.DatabaseConfig
import fr.olebo.persistence.services.DatabaseService
import fr.olebo.persistence.tests.testConnectionString
import org.junit.jupiter.api.assertDoesNotThrow
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import kotlin.test.BeforeTest
import kotlin.test.Test

internal class DatabaseServiceTests {
    private lateinit var di: DI

    private lateinit var databaseService: DatabaseService

    @BeforeTest
    fun initialize() {
        di = DI {
            bindSingleton {
                object : DatabaseConfig {
                    override val connectionString = testConnectionString
                }
            }
        }

        databaseService = DatabaseService(di)
    }

    @Test
    fun `get database connection`() {
        assertDoesNotThrow { databaseService.database }
    }
}