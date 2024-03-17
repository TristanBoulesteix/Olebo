package fr.olebo.persistence.tests.tables

import fr.olebo.persistence.tables.BlueprintTable
import fr.olebo.persistence.tests.ColumnData
import fr.olebo.persistence.tests.checkColumnsOf
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.kodein.di.direct
import org.kodein.di.instance
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class BlueprintTableTests : TableTests<BlueprintTable>(BlueprintTable) {
    @Test
    fun `check all column and table in database`() {
        // Step 1
        transaction {
            assertContains(SchemaUtils.listTables(), table.tableName)
        }

        val columns = checkColumnsOf(table.tableName)

        assertContains(columns, ColumnData.intId)
        assertContains(columns, ColumnData("name", "VARCHAR", length = 50))
        assertContains(columns, ColumnData("sprite", "VARCHAR", length = 200))
        assertContains(columns, ColumnData("HP", "INT", isNullable = true, length = 2000000000))
        assertContains(columns, ColumnData("MP", "INT", isNullable = true, length = 2000000000))
        assertContains(columns, ColumnData("id_type", "INT", length = 2000000000))
    }

    @Test
    fun `initialize pointers`(): Unit = transaction {
        table.initialize(di.direct.instance())

        checkPointer("@pointerTransparent", "pointer_transparent.png")
        checkPointer("@pointerBlue", "pointer_blue.png")
        checkPointer("@pointerWhite", "pointer_white.png")
        checkPointer("@pointerGreen", "pointer_green.png")
    }

    private fun checkPointer(name: String, sprite: String) {
        val query = table.selectAll().where(table.name eq name)
        assertEquals(1, query.count())

        val result = query.first()

        assertEquals(sprite, result[table.sprite])
        assertEquals(4, result[table.idType].value)
        assertNull(result[table.MP])
        assertNull(result[table.HP])
    }
}