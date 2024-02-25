package fr.olebo.persistence.tests.tables

import fr.olebo.domain.model.system.OleboConfiguration
import fr.olebo.persistence.tables.BaseInfo
import fr.olebo.persistence.tests.ColumnData
import fr.olebo.persistence.tests.checkColumnsOf
import fr.olebo.persistence.tests.jdbcConnection
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.kodein.di.DI
import org.kodein.di.bindProvider
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

internal class BaseInfoTests : TableTests<BaseInfo>({ BaseInfo(it) }) {
    override fun DI.MainBuilder.initializeDI() {
        bindProvider { OleboConfiguration("10.0.0", 10) }
    }

    @Test
    fun `check all column and table in database`() {
        // Step 1
        transaction {
            assertContains(SchemaUtils.listTables(), table.tableName)
        }

        val columns = checkColumnsOf(table.tableName)

        assertContains(columns, ColumnData("key_info", "VARCHAR", isPrimary = true, length = 50))
        assertContains(columns, ColumnData("value", "VARCHAR", length = 50))
    }

    @Test
    fun `get database version`() {
        fun setAndCheckVersionBaseFor(version: Int) {
            jdbcConnection.use {
                val statement = it.prepareStatement("INSERT OR REPLACE INTO BaseInfo(key_info, value) VALUES (?, ?)")
                statement.setString(1, BaseInfo.BASE_VERSION)
                statement.setInt(2, version)
                statement.execute()
            }

            checkVersionBaseFor(version)
        }

        setAndCheckVersionBaseFor(1)
        setAndCheckVersionBaseFor(10)
        setAndCheckVersionBaseFor(428)
    }

    @Test
    fun `initialize table`() {
        // We repeat the test to check for insert and update
        repeat(2) {
            table.initialize()

            checkVersionBaseFor(10)
        }
    }

    private fun checkVersionBaseFor(version: Int) {
        val actualValue = table.versionBase

        assertNotNull(actualValue)
        assertEquals(version, actualValue)
    }
}