package jdr.exia.model.dao

import jdr.exia.OLEBO_VERSION_CODE
import jdr.exia.localization.*
import jdr.exia.model.element.Element
import jdr.exia.model.tools.DatabaseException
import jdr.exia.system.OLEBO_DIRECTORY
import jdr.exia.update.checkForUpdate
import jdr.exia.update.downloadAndExit
import jdr.exia.view.tools.showConfirmMessage
import jdr.exia.view.tools.windowAncestor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import java.sql.Connection
import javax.swing.JButton
import javax.swing.JOptionPane
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

            transaction {
                // Check database version
                SchemaUtils.createMissingTablesAndColumns(BaseInfo)

                val version = BaseInfo.versionBase

                if (version == null || OLEBO_VERSION_CODE >= version || showUpdateMessageWarn(version)) {
                    BaseInfo.initialize()

                    val oleboTables = tables

                    SchemaUtils.createMissingTablesAndColumns(*oleboTables)
                    oleboTables.forEach {
                        if (it is Initializable)
                            it.initialize()
                    }

                    dropLegacyTables()

                    // Delete all elements that where removed from scenes
                    launch { transaction { Element.find { InstanceTable.deleted eq true }.forEach(Element::delete) } }
                }
            }
        }
    } catch (e: Exception) {
        throw DatabaseException(e)
    }

    @Suppress("KotlinConstantConditions")
    private fun showUpdateMessageWarn(versionCode: Int): Boolean {
        var userContinue = false

        val code = runBlocking(Dispatchers.IO) {
            var release = checkForUpdate(versionCode)

            if (release.isFailure) {
                release = checkForUpdate()
            }

            release.getOrNull()?.versionId ?: versionCode
        }

        val update = JButton(StringLocale[STR_UPDATE]).apply {
            this.addActionListener {
                windowAncestor?.dispose()
                runBlocking {
                    downloadAndExit(
                        onExitSuccess = { exitProcess(0) },
                        onDownloadFailure = { error("Unable to update") },
                        versionCode = code
                    )
                }
            }

        }

        val reset = JButton(StringLocale[STR_RESET]).apply {
            this.addActionListener {
                windowAncestor?.dispose()
                showConfirmMessage(
                    windowAncestor,
                    StringLocale[ST_WARNING_CONFIG_RESET],
                    StringLocale[STR_RESET],
                    confirm = true
                ) {
                    reset()
                    restart()
                }
            }
        }

        val continueButton = JButton(StringLocale[STR_CONTINUE]).apply {
            this.addActionListener {
                windowAncestor?.dispose()
                userContinue = true
            }
        }

        val exit = JButton(StringLocale[STR_EXIT]).apply { this.addActionListener { windowAncestor?.dispose() } }

        JOptionPane.showOptionDialog(
            null,
            StringLocale[ST_DB_VERSION_MISMATCH_MESSAGE],
            StringLocale[STR_DB_VERSION_MISMATCH],
            JOptionPane.NO_OPTION,
            JOptionPane.ERROR_MESSAGE,
            null,
            arrayOf(update, reset, continueButton, exit),
            exit
        )

        return userContinue || exitProcess(100)
    }

    private fun dropLegacyTables() = launch {
        transaction {
            val legacyTables = buildList {
                add(object : Table("Priority") {})
            }.toTypedArray()
            SchemaUtils.drop(*legacyTables)
        }
    }
}