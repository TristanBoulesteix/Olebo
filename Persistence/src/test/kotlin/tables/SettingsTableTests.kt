package fr.olebo.persistence.tests.tables

import fr.olebo.persistence.tables.SettingsTable
import fr.olebo.persistence.tests.ColumnData
import fr.olebo.persistence.tests.checkColumnsOf
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

internal class SettingsTableTests : TableTests<SettingsTable>({ SettingsTable() }) {
    @Test
    fun `check all column and table in database`() {
        // Step 1
        transaction {
            assertContains(SchemaUtils.listTables(), table.tableName)
        }

        val columns = checkColumnsOf(table.tableName)

        assertContains(columns, ColumnData("name", "VARCHAR", length = 255))
        assertContains(columns, ColumnData("value", "VARCHAR", length = 255, defaultValue = ""))
    }

    @Test
    fun `check settings initialization`() = transaction {
        table.initialize()

        table.initialValues.forEach { (id, name, value) ->
            val query = table.selectAll().where((table.name eq name) and (table.id eq id))

            assertEquals(1, query.count())

            val actualValue = query.first()[table.value]

            assertEquals(value.toString(), actualValue)
        }
    }

    @Test
    fun `check reset`() {
        transaction {
            repeat(20) { iteration ->
                table.insert {
                    it[id] = iteration
                    it[name] = "name $iteration"
                }
            }
        }

        table.reset()

        `check settings initialization`()
    }
}