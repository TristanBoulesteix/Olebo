package fr.olebo.persistence.tests.tables

import fr.olebo.persistence.tables.SceneTable
import fr.olebo.persistence.tests.ColumnData
import fr.olebo.persistence.tests.checkColumnsOf
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.test.Test
import kotlin.test.assertContains

internal class SceneTableTests: TableTests<SceneTable>(SceneTable) {
    @Test
    fun `check all column and table in database`() {
        // Step 1
        transaction {
            assertContains(SchemaUtils.listTables(), table.tableName)
        }

        val columns = checkColumnsOf(table.tableName)

        assertContains(columns, ColumnData.intId)
        assertContains(columns, ColumnData("name", "VARCHAR", length = 50))
        assertContains(columns, ColumnData("background", "VARCHAR", length = 200))
        assertContains(columns, ColumnData("id_act", "INT", length = 2000000000))
    }
}