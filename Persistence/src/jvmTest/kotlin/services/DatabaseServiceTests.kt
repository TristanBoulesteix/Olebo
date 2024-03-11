package fr.olebo.persistence.tests.services

import fr.olebo.domain.coroutine.ApplicationIoScope
import fr.olebo.persistence.DatabaseConfig
import fr.olebo.persistence.services.DatabaseService
import fr.olebo.persistence.tests.buildMockedPath
import fr.olebo.persistence.tests.jdbcConnection
import fr.olebo.persistence.tests.model.InitializableTestTable
import fr.olebo.persistence.tests.model.TestTable
import fr.olebo.persistence.tests.testConnectionString
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.kodein.di.*
import java.sql.Connection
import kotlin.test.*

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
            bindProvider<List<Table>> { listOf(TestTable(), InitializableTestTable()) }
            bindSingleton<ApplicationIoScope> {
                object : ApplicationIoScope, CoroutineScope by CoroutineScope(StandardTestDispatcher()) {}
            }
        }

        connection = jdbcConnection

        databaseService = DatabaseService(di)
    }

    @Test
    fun `get database connection`() {
        databaseService.database
    }

    @Test
    fun `test that legacy tables are removed from the database`() = runTest {
        jdbcConnection.use {
            it.prepareStatement("CREATE TABLE Priority(id INT)").execute()
            it.prepareStatement("CREATE TABLE Test(id INT)").execute()
        }

        databaseService.dropLegacyTables()

        newSuspendedTransaction { assertTrue(SchemaUtils.listTables().size == 2) }
    }

    @Test
    fun `initialize tables that implements Initializable interface`() {
        val table = di.direct.instance<List<Table>>().filterIsInstance<InitializableTestTable>().first()

        transaction {
            val result = table.selectAll()

            assertEquals(1, result.count())

            val firstResult = result.first()

            assertEquals("test", firstResult[table.field1])
            assertEquals(2, firstResult[table.id].value)
        }
    }

    @AfterTest
    fun clear() {
        connection.close()
    }
}