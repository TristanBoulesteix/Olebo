package fr.olebo.persistence.services

import fr.olebo.domain.coroutine.ApplicationIoScope
import fr.olebo.domain.models.Configurations
import fr.olebo.domain.models.get
import fr.olebo.persistence.DatabaseConfiguration
import fr.olebo.persistence.LegacyTables
import fr.olebo.persistence.tables.Initializable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.Connection
import kotlin.io.path.createFile
import kotlin.io.path.createParentDirectories
import kotlin.io.path.exists

internal class DatabaseService(
    configuration: Configurations,
    scope: ApplicationIoScope,
    tables: List<Table>,
    legacyTables: LegacyTables
) {
    internal val database: Database

    init {
        val databaseConfiguration = configuration.get<DatabaseConfiguration>()

        databaseConfiguration.databaseFilePath.apply {
            if (!exists()) {
                createParentDirectories()
                createFile()
            }
        }

        database = Database.connect(databaseConfiguration.connectionString, "org.sqlite.JDBC").apply {
            TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
        }

        scope.launch {
            dropLegacyTables(legacyTables)
        }

        transaction {
            SchemaUtils.createMissingTablesAndColumns(*tables.toTypedArray())

            tables.forEach {
                if (it is Initializable) {
                    it.initialize(configuration)
                }
            }
        }
    }

    suspend fun dropLegacyTables(legacyTables: List<Table>) = newSuspendedTransaction(Dispatchers.IO) {
        SchemaUtils.drop(*legacyTables.toTypedArray())
    }
}