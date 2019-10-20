package jdr.exia.model.dao

import jdr.exia.model.act.Act
import jdr.exia.model.element.Blueprint
import jdr.exia.model.element.Character
import jdr.exia.model.element.Element
import jdr.exia.model.element.Type
import jdr.exia.model.utils.MessageException
import jdr.exia.model.utils.OLEBO_DIRECTORY
import jdr.exia.model.utils.toInt
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import java.sql.Connection

object DAO {
    private const val DATABASE_NAME = "database.db"

    private val filePath = OLEBO_DIRECTORY + "db${File.separator}$DATABASE_NAME"
    private val url = "jdbc:sqlite:$filePath"

    val database: Database

    init {
        if (!File(filePath).exists()) {
            File(this.javaClass.classLoader.getResource("db/template.db")!!.toURI()).copyTo(
                File(filePath), true
            )
        }

        database = Database.connect(url, "org.sqlite.JDBC")

        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
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

    fun getElementsWithType(type: Type): MutableList<Blueprint> {
        return transaction {
            Blueprint.find {
                BlueprintTable.idType eq type.type.id.value
            }.toCollection(mutableListOf())
        }
    }

    fun getElementsWithIdScene(idScene: Int): MutableList<Element> {
        return transaction {
            Element.buildElementsFromRequest(
                InstanceTable.select { InstanceTable.idScene eq idScene }.toCollection(
                    mutableListOf()
                ), idScene
            )
        }
    }

    fun saveNewElement(element: Element) {
        transaction {
            InstanceTable.insert {
                it[idBlueprint] = element.idBlueprint
                it[idScene] = element.idScene
                it[size] = element.size.name
                it[visible] = element.visible.toInt()
                it[x] = element.position.x
                it[y] = element.position.y

                if (element is Character) {
                    it[currentHP] = element.currentHealth
                    it[currentMP] = element.currentMana
                }
            }
        }
    }

    fun updateElement(element: Element) {
        transaction {
            InstanceTable.update({ InstanceTable.id eq element.idInstance }) {
                it[size] = element.size.name
                it[visible] = element.visible.toInt()
                it[x] = element.position.x
                it[y] = element.position.y

                if (element is Character) {
                    it[currentHP] = element.currentHealth
                    it[currentMP] = element.currentMana
                }
            }
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

    /**
     * Delete an element in the database
     *
     * @param id The element id to delete
     * @param table The table where the element is from
     */
    fun deleteWithId(id: Int, table: IntIdTable) {
        transaction {
            table.deleteWhere { table.id eq id }
        }
    }
}