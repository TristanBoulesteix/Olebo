package fr.olebo.persistence.services

import fr.olebo.persistence.DatabaseConfig
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.constant
import org.kodein.di.instance
import java.sql.Connection
import kotlin.io.path.createFile
import kotlin.io.path.createParentDirectories
import kotlin.io.path.exists

internal class DatabaseService(override val di: DI) : DIAware {
    private val configuration by instance<DatabaseConfig>()

    val database: Database

    init {
        configuration.databaseFilePath.apply {
            if (!exists()) {
                createParentDirectories()
                createFile()
            }
        }

        database = Database.connect(configuration.connectionString, "org.sqlite.JDBC").apply {
            TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
        }
    }

    suspend fun dropLegacyTables() = newSuspendedTransaction(Dispatchers.IO) {
        val legacyTablesName by constant<List<String>>()
        val legacyTables = legacyTablesName.map { object : Table(it) {} }.toTypedArray()
        SchemaUtils.drop(*legacyTables)
    }
}