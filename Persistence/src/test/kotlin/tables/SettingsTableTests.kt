package fr.olebo.persistence.tests.tables

import fr.olebo.domain.models.LabelVisibility
import fr.olebo.domain.models.SerializableColor
import fr.olebo.persistence.tables.SettingsInitialValue
import fr.olebo.persistence.tables.SettingsTable
import fr.olebo.persistence.tables.SettingsTable.Companion.AUTO_UPDATE
import fr.olebo.persistence.tables.SettingsTable.Companion.CHANGELOGS_VERSION
import fr.olebo.persistence.tables.SettingsTable.Companion.CURRENT_LANGUAGE
import fr.olebo.persistence.tables.SettingsTable.Companion.CURSOR_COLOR
import fr.olebo.persistence.tables.SettingsTable.Companion.CURSOR_ENABLED
import fr.olebo.persistence.tables.SettingsTable.Companion.DEFAULT_ELEMENT_VISIBILITY
import fr.olebo.persistence.tables.SettingsTable.Companion.LABEL_COLOR
import fr.olebo.persistence.tables.SettingsTable.Companion.LABEL_STATE
import fr.olebo.persistence.tables.SettingsTable.Companion.PLAYER_FRAME_ENABLED
import fr.olebo.persistence.tables.SettingsTable.Companion.SHOULD_OPEN_PLAYER_WINDOW_IN_FULL_SCREEN
import fr.olebo.persistence.tables.SettingsTable.Companion.UPDATE_WARN
import fr.olebo.persistence.tests.ColumnData
import fr.olebo.persistence.tests.checkColumnsOf
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.kodein.di.DI
import org.kodein.di.bindConstant
import org.kodein.di.direct
import org.kodein.di.instance
import java.util.*
import kotlin.test.*

internal class SettingsTableTests : TableTests<SettingsTable>({ SettingsTable(it) }) {
    override fun DI.MainBuilder.initializeDI() {
        bindConstant("defaultLabelColor") { Json.encodeToString<SerializableColor>(SerializableColor.BLACK) }
        bindConstant("defaultLabelVisibility") { Json.encodeToString<LabelVisibility>(LabelVisibility.OnlyForMaster) }
    }

    private lateinit var initialValues: List<SettingsInitialValue>

    @BeforeTest
    fun testInitialize() {
        initialValues = listOf(
            SettingsInitialValue(2, AUTO_UPDATE, true),
            SettingsInitialValue(3, UPDATE_WARN, ""),
            SettingsInitialValue(4, CURSOR_ENABLED, true),
            SettingsInitialValue(5, CURRENT_LANGUAGE, Locale.getDefault().language),
            SettingsInitialValue(6, CURSOR_COLOR, ""),
            SettingsInitialValue(7, PLAYER_FRAME_ENABLED, false),
            SettingsInitialValue(8, DEFAULT_ELEMENT_VISIBILITY, false),
            SettingsInitialValue(9, LABEL_STATE, di.direct.instance<String>("defaultLabelVisibility")),
            SettingsInitialValue(10, LABEL_COLOR, di.direct.instance<String>("defaultLabelColor")),
            SettingsInitialValue(11, CHANGELOGS_VERSION, ""),
            SettingsInitialValue(12, SHOULD_OPEN_PLAYER_WINDOW_IN_FULL_SCREEN, true),
        )
    }

    /**
     * If this test fails, the value that will be set in the database may brake retro-compatibility with previous version of Olebo.
     */
    @Test
    fun `validate initial values`() {
        assertContentEquals(initialValues, table.initialValues)
    }

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

        initialValues.forEach { (id, name, value) ->
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