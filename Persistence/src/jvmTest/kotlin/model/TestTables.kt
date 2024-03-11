package fr.olebo.persistence.tests.model

import fr.olebo.persistence.tables.Initializable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction

class TestTable : IntIdTable("BasicTable")

class InitializableTestTable: IntIdTable("InitializableTable"), Initializable {
    val field1 = varchar("field", 200)

    override fun initialize(): Unit = transaction {
        insert {
            it[field1] = "test"
            it[id] = 2
        }
    }
}