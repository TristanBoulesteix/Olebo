package fr.olebo.persistence.tests.tables

import fr.olebo.persistence.tables.BaseInfo
import fr.olebo.persistence.tests.ColumnData
import fr.olebo.persistence.tests.checkColumnsOf
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.test.Test
import kotlin.test.assertContains

internal class BaseInfoTests : TableTests({ BaseInfo() }) {
    @Test
    fun `check all column and table in database`() {
        // Step 1
        transaction {
            assertContains(SchemaUtils.listTables(), table.tableName)
        }

        val columns = database.checkColumnsOf(table.tableName)

        assertContains(columns, ColumnData("key_info", "VARCHAR", isPrimary = true))
        assertContains(columns, ColumnData("value", "VARCHAR"))
    }
}