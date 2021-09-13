package jdr.exia.model.dao

import jdr.exia.OLEBO_VERSION_CODE
import jdr.exia.localization.*
import jdr.exia.model.element.Element
import jdr.exia.model.tools.DatabaseException
import jdr.exia.system.OLEBO_DIRECTORY
import jdr.exia.update.downloadAndExit
import jdr.exia.view.tools.showConfirmMessage
import jdr.exia.view.tools.windowAncestor
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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

object DAO {
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

            transaction {
                // Check database version
                SchemaUtils.createMissingTablesAndColumns(BaseInfo)

                val version = BaseInfo.versionBase

                if (version == null || OLEBO_VERSION_CODE >= version || showUpdateMessageWarn()) {
                    BaseInfo.initialize()

                    SchemaUtils.createMissingTablesAndColumns(*tables)
                    SchemaUtils.drop(object : Table("Priority") {})
                    tables.forEach {
                        if (it is Initializable)
                            it.initialize()
                    }

                    // Delete all elements that where removed from scenes
                    Element.find { InstanceTable.deleted eq true }.forEach(Element::delete)
                }
            }
        }
    } catch (e: Exception) {
        throw DatabaseException(e)
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun showUpdateMessageWarn(): Boolean {
        var userContinue = false

        val update = JButton(StringLocale[STR_UPDATE]).apply {
            this.addActionListener {
                windowAncestor?.dispose()
                GlobalScope.launch {
                    downloadAndExit(
                        onExitSuccess = { exitProcess(0) },
                        onDownloadFailure = { error("Unable to update") }
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

        return if (!userContinue) exitProcess(100) else userContinue
    }
}