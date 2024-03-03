package fr.olebo.persistence.tests.services

import fr.olebo.persistence.DatabaseConfig
import fr.olebo.persistence.services.DatabaseService
import fr.olebo.persistence.tests.buildMockedPath
import fr.olebo.persistence.tests.jdbcConnection
import fr.olebo.persistence.tests.testConnectionString
import kotlinx.coroutines.test.runTest
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.kodein.di.DI
import org.kodein.di.bindProvider
import org.kodein.di.bindSingleton
import java.sql.Connection
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

internal class DatabaseServiceTests {
    private lateinit var di: DI

    private lateinit var databaseService: DatabaseService

    private lateinit var connection: Connection

    @BeforeTest
    fun initialize() {
        di = DI {
            bindSingleton {
                object : DatabaseConfig {
                    override val connectionString = testConnectionString

                    override val databaseFilePath = buildMockedPath()
                }
            }
            bindProvider("legacyTablesName") { listOf("Priority", "Test") }
        }

        connection = jdbcConnection

        databaseService = DatabaseService(di)
    }

    @Test
    fun `get database connection`() {
        //assertDoesNotThrow { databaseService.database }
    }

    @Test
    fun `test that legacy tables are removed from the database`() = runTest {
        jdbcConnection.use {
            it.prepareStatement("CREATE TABLE Priority(id INT)").execute()
            it.prepareStatement("CREATE TABLE Test(id INT)").execute()
        }

        databaseService.dropLegacyTables()

        newSuspendedTransaction { assertTrue(SchemaUtils.listTables().isEmpty()) }
    }

    @AfterTest
    fun clear() {
        connection.close()
    }
}