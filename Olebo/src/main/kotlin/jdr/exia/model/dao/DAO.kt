package jdr.exia.model.dao

import jdr.exia.localization.*
import jdr.exia.model.element.Element
import jdr.exia.updater.forceUpdateAndRestart
import jdr.exia.updater.runJar
import jdr.exia.utils.DatabaseException
import jdr.exia.view.legacy.utils.showConfirmMessage
import jdr.exia.view.legacy.utils.windowAncestor
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import java.sql.Connection
import javax.swing.JButton
import javax.swing.JOptionPane
import kotlin.system.exitProcess

object DAO {
    /**
     * Version of the database
     *
     * Must be incremented each time the database structure is modified
     */
    const val DATABASE_VERSION = 4

    const val DATABASE_NAME = "database.db"

    private val filePath = "${OLEBO_DIRECTORY}db${File.separator}$DATABASE_NAME"
    private val url = "jdbc:sqlite:$filePath"

    val database: Database = try {
        File(filePath).apply {
            this.parentFile.mkdirs()
            this.createNewFile()
        }

        // Initialisation of data and create database connection
        Database.connect(url, "org.sqlite.JDBC").also {
            TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE

            val result = transaction {
                // Check database version
                SchemaUtils.createMissingTablesAndColumns(SettingsTable)

                val version =
                    SettingsTable.select(SettingsTable.baseVersionWhere).firstOrNull()?.getOrNull(SettingsTable.value)
                        ?.toIntOrNull()

                if (version != null && DATABASE_VERSION < version)
                    return@transaction false

                SettingsTable.initialize()

                SchemaUtils.createMissingTablesAndColumns(*tables)
                tables.forEach {
                    if (it is Initializable)
                        it.initialize()
                }
                // Delete all elements that where removed from scenes
                Element.find { InstanceTable.deleted eq true }.forEach(Element::delete)

                return@transaction true
            }

            // If result is false, the database version does not match
            if (!result) {
                showUpdateMessageWarn()
                exitProcess(100)
            }
        }

    } catch (e: Exception) {
        throw DatabaseException(e)
    }

    private fun showUpdateMessageWarn() {
        val update = JButton(StringLocale[STR_UPDATE]).apply {
            this.addActionListener {
                windowAncestor?.dispose()
                forceUpdateAndRestart()
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
                    runJar(jarPath)
                    exitProcess(0)
                }
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
            arrayOf(update, reset, exit),
            exit
        )
    }

}