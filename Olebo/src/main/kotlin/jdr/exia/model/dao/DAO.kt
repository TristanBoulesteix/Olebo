package jdr.exia.model.dao

import jdr.exia.model.act.Act
import jdr.exia.model.dao.tables.ActTable
import jdr.exia.utils.appDatas
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SizedIterable
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
    fun getActsList(): Array<String> {
        return transaction {
            ActTable.selectAll().withDistinct().map {
                it[ActTable.name]
            }.toTypedArray()
        }
    }

    /**
     * Get an instance of a selected act with its ID
     */
    fun getActWithId(idAct: Int): Act {
        return transaction {
            Act.findById(idAct)!!
        }
    }
}

fun <T> SizedIterable<T>.getContent(): MutableList<T> {
    val content = mutableListOf<T>()

    this.forEach {
        content += it
    }

    return content
}