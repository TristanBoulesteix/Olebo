package model.dao

import model.act.Act
import model.element.Blueprint
import model.element.Type
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import utils.DatabaseException
import utils.MessageException
import java.io.File
import java.sql.Connection

object DAO {
    private const val DATABASE_NAME = "database.db"

    private val filePath = OLEBO_DIRECTORY + "db${File.separator}$DATABASE_NAME"
    private val url = "jdbc:sqlite:$filePath"

    val database: Database

    init {
        database = try {
            File(filePath).apply {
                this.parentFile.mkdirs()
                this.createNewFile()
            }

            Database.connect(url, "org.sqlite.JDBC").also {
                TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE

                transaction {
                    SchemaUtils.createMissingTablesAndColumns(*tables)
                    tables.forEach {
                        if (it is Initializable)
                            it.initialize()
                    }
                }
            }
        } catch (e: Exception) {
            throw  DatabaseException(e)
        }
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
            Act.findById(idAct) ?: throw MessageException("Error ! This act doesn't exist.")
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