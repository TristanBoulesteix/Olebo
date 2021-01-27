package jdr.exia.model.dao

import jdr.exia.localization.*
import jdr.exia.model.act.Act
import jdr.exia.model.element.Blueprint
import jdr.exia.model.element.Element
import jdr.exia.model.element.Type
import jdr.exia.updater.forceUpdateAndRestart
import jdr.exia.updater.runJar
import jdr.exia.utils.DatabaseException
import jdr.exia.utils.MessageException
import jdr.exia.view.utils.showConfirmMessage
import jdr.exia.view.utils.windowAncestor
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.sql.*
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

    private val filePath = OLEBO_DIRECTORY + "db${File.separator}$DATABASE_NAME"
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
        val update = JButton(Strings[STR_UPDATE]).apply {
            this.addActionListener {
                windowAncestor?.dispose()
                forceUpdateAndRestart()
            }
        }

        val reset = JButton(Strings[STR_RESET]).apply {
            this.addActionListener {
                windowAncestor?.dispose()
                showConfirmMessage(windowAncestor, Strings[ST_WARNING_CONFIG_RESET], Strings[STR_RESET]) {
                    reset()
                    runJar(jarPath)
                    exitProcess(0)
                }
            }
        }

        val exit = JButton(Strings[STR_EXIT]).apply { this.addActionListener { windowAncestor?.dispose() } }

        JOptionPane.showOptionDialog(
            null,
            Strings[ST_DB_VERSION_MISMATCH_MESSAGE],
            Strings[STR_DB_VERSION_MISMATCH],
            JOptionPane.NO_OPTION,
            JOptionPane.ERROR_MESSAGE,
            null,
            arrayOf(update, reset, exit),
            exit
        )
    }

    /**
     * Get all acts stored into the database.
     *
     * Acts are stored as pairs. The first part is the ID and the second is the name
     */
    fun getActsList(): Array<Pair<String, String>> {
        return transaction {
            ActTable.slice(ActTable.id, ActTable.name).selectAll().map {
                it[ActTable.id].toString() to it[ActTable.name]
            }.toTypedArray()
        }
    }

    /**
     * Get an instance of a selected act with its ID
     */
    fun getActWithId(idAct: Int): Act {
        return transaction {
            Act.findById(idAct) ?: throw MessageException(Strings[ST_ERROR_ACT_NOT_EXISTS])
        }
    }

    /**
     * Get all elements with a given type
     *
     * @param type The wanted type of elements
     *
     * @return A MutableList of Blueprints
     */
    fun getElementsWithType(type: Type): MutableList<Blueprint> {
        return transaction {
            Blueprint.find {
                BlueprintTable.idType eq type.type.id.value
            }.toCollection(mutableListOf())
        }
    }

    /**
     * Delete an element in the database
     *
     * @param entity The entity to delete
     */
    fun deleteEntity(entity: Entity<Int>) {
        transaction {
            entity.delete()
        }
    }
}