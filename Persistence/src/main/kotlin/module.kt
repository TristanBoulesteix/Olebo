package fr.olebo.persistence

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.kodein.di.DI
import org.kodein.di.bindEagerSingleton
import org.kodein.di.instance
import java.io.File
import java.sql.Connection

val persistenceModule by DI.Module {
    bindEagerSingleton {
        val filePath = "${instance<String>("olebo-directory")}db${File.separator}database.db"
        val connectionString = "jdbc:sqlite:$filePath"

        File(filePath).apply {
            parentFile.mkdirs()
            createNewFile()
        }

        Database.connect(connectionString, "org.sqlite.JDBC").apply {
            TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
        }
    }
}