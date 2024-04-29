package fr.olebo.persistence.tests.tables

import fr.olebo.persistence.tables.TagTable
import fr.olebo.persistence.tests.ColumnData
import fr.olebo.persistence.tests.checkColumnsOf
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.test.Test
import kotlin.test.assertContains

internal class TagTableTests : TableTests<TagTable>(TagTable) {
    @Test
    fun `check all column and table in database`() {
        transaction {
            assertContains(SchemaUtils.listTables(), table.tableName)
        }

        val columns = checkColumnsOf(table.tableName)

        assertContains(columns, ColumnData("tagValue", "VARCHAR", length = 40, isPrimary = true))
    }
}