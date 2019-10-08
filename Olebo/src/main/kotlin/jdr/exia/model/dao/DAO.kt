package jdr.exia.model.dao

import jdr.exia.model.act.Act
import jdr.exia.model.utils.MessageException
import jdr.exia.model.utils.appDatas
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import java.io.File.separator
import java.sql.Connection

object DAO {
    private val dbName = "Olebo${separator}db${separator}database.db"
    private val filePath = "$appDatas$dbName"
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
}