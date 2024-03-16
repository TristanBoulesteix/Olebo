package fr.olebo.persistence.tests.tables

import fr.olebo.persistence.tables.ActTable
import fr.olebo.persistence.tests.ColumnData
import fr.olebo.persistence.tests.checkColumnsOf
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.test.Test
import kotlin.test.assertContains

internal class ActTableTests : TableTests<ActTable>(ActTable) {
    @Test
    fun `check all column and table in database`() {
        // Step 1
        transaction {
            assertContains(SchemaUtils.listTables(), table.tableName)
        }

        val columns = checkColumnsOf(table.tableName)

        assertContains(columns, ColumnData.intId)
        assertContains(columns, ColumnData("name", "VARCHAR", length = 50))
        assertContains(columns, ColumnData("id_scene", "INT", length = 2000000000, defaultValue = 0.toString()))
    }
}