package fr.olebo.persistence.tests.tables

import fr.olebo.domain.Constants
import fr.olebo.domain.coroutine.ApplicationIoScope
import fr.olebo.domain.models.ConfigurationItem
import fr.olebo.domain.models.appendConfiguration
import fr.olebo.persistence.DatabaseConfiguration
import fr.olebo.persistence.LegacyTables
import fr.olebo.persistence.services.DatabaseService
import fr.olebo.persistence.tables.EnumInitializable
import fr.olebo.persistence.tests.buildMockedPath
import fr.olebo.persistence.tests.jdbcConnection
import fr.olebo.persistence.tests.testConnectionString
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.test.StandardTestDispatcher
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.kodein.di.*
import java.nio.file.Path
import java.sql.Connection
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

internal abstract class TableTests<T : Table>(protected val table: T) {
    protected lateinit var di: DI
        private set

    private lateinit var connection: Connection

    open fun DI.MainBuilder.initializeDI() = Unit

    data class ActualDatabaseConfiguration(override val connectionString: String, override val databaseFilePath: Path) :
        DatabaseConfiguration

    @BeforeTest
    fun tableInitialize() {
        connection = jdbcConnection

        val databasePath = buildMockedPath()

        di = DI {
            bindSingletonOf(::DatabaseService)
            bindSet<ConfigurationItem> {
                add { provider { Constants("defaultLabelColor", "defaultLabelVisibility") } }
            }
            bindSingleton<ApplicationIoScope> {
                object : ApplicationIoScope, CoroutineScope by CoroutineScope(StandardTestDispatcher()) {}
            }
            bindProviderOf<Array<Table>>(::arrayOf)
            bindProvider<LegacyTables> { object : LegacyTables, List<Table> by listOf() {} }
            appendConfiguration {
                ActualDatabaseConfiguration(testConnectionString, databasePath)
            }

            initializeDI()
        }

        transaction(di.direct.instance<DatabaseService>().database) {
            SchemaUtils.create(table)
        }
    }

    @AfterTest
    fun clear() {
        connection.close()
    }
}

internal abstract class EnumInitializableTests<E : Enum<E>, T : EnumInitializable<E>>(table: T) : TableTests<T>(table) {
    @Test
    fun `check initialization`() = transaction {
        table.initialize(di.direct.instance())

        table.values.forEachIndexed { index, enum ->
            val idEnum = index + 1

            val query = table.selectAll().where { (table.id eq idEnum) and (table.enumValue eq enum) }
            assertEquals(1, query.count())
        }
    }
}