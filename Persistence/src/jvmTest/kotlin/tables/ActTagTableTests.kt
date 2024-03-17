package fr.olebo.persistence.tests.tables

import fr.olebo.persistence.tables.ActTagTable
import fr.olebo.persistence.tests.ColumnData
import fr.olebo.persistence.tests.checkColumnsOf
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.test.Test
import kotlin.test.assertContains

class ActTagTableTests : TableTests<ActTagTable>(ActTagTable) {
    @Test
    fun `check all column and table in database`() {
        transaction {
            assertContains(SchemaUtils.listTables(), table.tableName)
        }

        val columns = checkColumnsOf(table.tableName)

        assertContains(columns, ColumnData("act", "INT", length = 2000000000, isPrimary = true))
        assertContains(columns, ColumnData("tag", "VARCHAR", length = 40, isPrimary = true))
    }
}