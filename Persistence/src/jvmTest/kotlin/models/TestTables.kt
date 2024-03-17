package fr.olebo.persistence.tests.models

import fr.olebo.domain.models.Configurations
import fr.olebo.persistence.tables.Initializable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction

class TestTable : IntIdTable("BasicTable")

class InitializableTestTable: IntIdTable("InitializableTable"), Initializable {
    val field1 = varchar("field", 200)

    override fun initialize(configurations: Configurations): Unit = transaction {
        insert {
            it[field1] = "test"
            it[id] = 2
        }
    }
}