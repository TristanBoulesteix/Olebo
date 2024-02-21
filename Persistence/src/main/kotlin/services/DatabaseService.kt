package fr.olebo.persistence.services

import fr.olebo.persistence.DatabaseConfig
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.instance
import java.io.File
import java.sql.Connection

internal class DatabaseService(override val di: DI) : DIAware {
    private val configuration by instance<DatabaseConfig>()

    val database: Database

    init {
        val filePath = configuration.filePath

        if (filePath != null) {
            File(filePath).apply {
                parentFile.mkdirs()
                createNewFile()
            }
        }

        database = Database.connect(configuration.connectionString, "org.sqlite.JDBC").apply {
            TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
        }
    }
}