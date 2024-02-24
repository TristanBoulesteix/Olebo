package fr.olebo.persistence.tests.tables

import fr.olebo.persistence.tables.BaseInfo
import fr.olebo.persistence.tests.ColumnData
import fr.olebo.persistence.tests.checkColumnsOf
import fr.olebo.persistence.tests.jdbcConnection
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

internal class BaseInfoTests : TableTests<BaseInfo>({ BaseInfo() }) {
    @Test
    fun `check all column and table in database`() {
        // Step 1
        transaction {
            assertContains(SchemaUtils.listTables(), table.tableName)
        }

        val columns = checkColumnsOf(table.tableName)

        assertContains(columns, ColumnData("key_info", "VARCHAR", isPrimary = true))
        assertContains(columns, ColumnData("value", "VARCHAR"))
    }

    @Test
    fun `get database version`() {
        jdbcConnection.use {
            val statement = it.prepareStatement("INSERT OR REPLACE INTO BaseInfo(key_info, value) VALUES (?, ?)")
            statement.setString(1, BaseInfo.baseVersion)
            statement.setString(2, 1.toString())
            statement.executeUpdate()
        }

        val actualValue = table.versionBase

        assertNotNull(actualValue)
        assertEquals(1, actualValue)
    }
}