package fr.olebo.persistence.tests.tables

import fr.olebo.persistence.tables.InstanceTable
import fr.olebo.persistence.tests.ColumnData
import fr.olebo.persistence.tests.checkColumnsOf
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.test.Test
import kotlin.test.assertContains

internal class InstanceTableTests : TableTests<InstanceTable>(InstanceTable) {
    @Test
    fun `check all column and table in database`() {
        transaction {
            assertContains(SchemaUtils.listTables(), table.tableName)
        }

        val columns = checkColumnsOf(table.tableName)

        assertContains(columns, ColumnData.intId)
        assertContains(columns, ColumnData("current_HP", "INT", length = 2000000000, isNullable = true))
        assertContains(columns, ColumnData("current_MP", "INT", length = 2000000000, isNullable = true))
        assertContains(columns, ColumnData("x", "SINGLE", length = 2000000000, defaultValue = 10f.toString()))
        assertContains(columns, ColumnData("y", "SINGLE", length = 2000000000, defaultValue = 10f.toString()))
        assertContains(columns, ColumnData("ID_Size", "INT", length = 2000000000, defaultValue = 2.toString()))
        assertContains(columns, ColumnData("Visible", "BOOLEAN", length = 2000000000, defaultValue = 0.toString()))
        assertContains(columns, ColumnData("Orientation", "SINGLE", length = 2000000000, defaultValue = 0f.toString()))
        assertContains(columns, ColumnData("id_priority", "INT", length = 2000000000, defaultValue = 2.toString()))
        assertContains(columns, ColumnData("ID_Scene", "INT", length = 2000000000, defaultValue = 0.toString()))
        assertContains(columns, ColumnData("id_blueprint", "INT", length = 2000000000, defaultValue = 0.toString()))
        assertContains(columns, ColumnData("deleted", "BOOLEAN", length = 2000000000, defaultValue = 0.toString()))
        assertContains(columns, ColumnData("alias", "VARCHAR", length = 255, defaultValue = ""))
    }
}