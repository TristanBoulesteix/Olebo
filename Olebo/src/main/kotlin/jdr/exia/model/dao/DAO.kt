package jdr.exia.model.dao

import jdr.exia.model.act.Act
import jdr.exia.model.act.Scene
import jdr.exia.model.utils.MessageException
import jdr.exia.model.utils.appDatas
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import java.io.File.separator
import java.sql.Connection

object DAO {
    private val dbName = "Olebo${separator}db${separator}template.db"
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
     * Get all acts stored into the database
     */
    fun getActsList(result: Column<*> = ActTable.name): Array<String> {
        return transaction {
            ActTable.selectAll().withDistinct().map {
                it[result].toString()
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

fun main() {
    transaction(DAO.database) {
        DAO.getActWithId(1).scenes += Scene.new {
            name = "test1"
            background = "test_back"
            idAct = 1
        }
    }
}