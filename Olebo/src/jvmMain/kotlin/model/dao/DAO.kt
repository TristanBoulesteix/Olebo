package jdr.exia.model.dao

import jdr.exia.OLEBO_VERSION_CODE
import jdr.exia.model.element.Element
import jdr.exia.model.tools.DatabaseException
import jdr.exia.system.OLEBO_DIRECTORY
import jdr.exia.update.showErrorDatabaseUI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import java.sql.Connection
import kotlin.system.exitProcess

object DAO : CoroutineScope by CoroutineScope(Dispatchers.IO) {
    const val DATABASE_NAME = "database.db"

    private val filePath = "${OLEBO_DIRECTORY}db${File.separator}$DATABASE_NAME"
    private val url = "jdbc:sqlite:$filePath"

    var database: Database = getDatabase()
        private set

    fun refreshDatabase() {
        database = getDatabase()
    }

    @JvmName("get")
    private fun getDatabase() = try {
        File(filePath).apply {
            this.parentFile.mkdirs()
            this.createNewFile()
        }

        // Initialisation of data and create database connection
        Database.connect(url, "org.sqlite.JDBC").also {
            TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE

            var version: Int? = null

            transaction {
                // Check database version
                SchemaUtils.createMissingTablesAndColumns(BaseInfo)

                version = BaseInfo.versionBase
            }

            if (version == null || OLEBO_VERSION_CODE >= version!! || showUpdateMessageWarn(version!!)) {
                transaction {
                    BaseInfo.initialize()

                    val oleboTables = tables

                    SchemaUtils.createMissingTablesAndColumns(*oleboTables)
                    oleboTables.forEach {
                        if (it is Initializable)
                            it.initialize()
                    }

                    dropLegacyTables()
                }

                // Delete all elements that where removed from scenes
                launch {
                    newSuspendedTransaction {
                        Element.find { InstanceTable.deleted eq true }.forEach(Element::delete)
                    }
                }
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
        throw DatabaseException(e)
    }

    @Suppress("KotlinConstantConditions")
    private fun showUpdateMessageWarn(versionCode: Int) = showErrorDatabaseUI(versionCode) || exitProcess(100)

    private fun dropLegacyTables() = launch {
        transaction {
            val legacyTables = buildList {
                add(object : Table("Priority") {})
            }.toTypedArray()
            SchemaUtils.drop(*legacyTables)
        }
    }
}