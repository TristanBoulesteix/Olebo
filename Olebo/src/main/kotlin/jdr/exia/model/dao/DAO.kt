package jdr.exia.model.dao

import jdr.exia.model.act.Act
import jdr.exia.localization.ST_ERROR_ACT_NOT_EXISTS
import jdr.exia.localization.Strings
import jdr.exia.model.element.Blueprint
import jdr.exia.model.element.Element
import jdr.exia.model.element.Type
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import jdr.exia.utils.DatabaseException
import jdr.exia.utils.MessageException
import java.io.File
import java.sql.Connection

object DAO {
    const val DATABASE_VERSION = 3
    const val DATABASE_NAME = "database.db"

    private val filePath = OLEBO_DIRECTORY + "db${File.separator}$DATABASE_NAME"
    private val url = "jdbc:sqlite:$filePath"

    val database: Database = try {
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
                // Delete all elements that where removed from scenes
                Element.find { InstanceTable.deleted eq true }.forEach(Element::delete)
            }
        }
    } catch (e: Exception) {
        throw  DatabaseException(e)
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