package fr.olebo.persistence.tables

import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.Locale

internal class SettingsTable : IntIdTable(), Initializable {
    val name = varchar("name", 255)

    val value = varchar("value", 255).default("")

    val initialValues
        get() = listOf(
            SettingsInitialValue(2, AUTO_UPDATE, true),
            SettingsInitialValue(3, UPDATE_WARN, ""),
            SettingsInitialValue(4, CURSOR_ENABLED, true),
            SettingsInitialValue(5, CURRENT_LANGUAGE, Locale.getDefault().language),
            SettingsInitialValue(6, CURSOR_COLOR, ""),
            SettingsInitialValue(7, PLAYER_FRAME_ENABLED, false),
            SettingsInitialValue(8, DEFAULT_ELEMENT_VISIBILITY, false),
            /*        SettingsInitialValue(9, LABEL_STATE, SerializableLabelState.ONLY_FOR_MASTER.encode()),
                    SettingsInitialValue(10, LABEL_COLOR, SerializableColor.BLACK.encode(),),*/
            SettingsInitialValue(11, CHANGELOGS_VERSION, ""),
            SettingsInitialValue(12, SHOULD_OPEN_PLAYER_WINDOW_IN_FULL_SCREEN, true),
        )

    override fun initialize() = transaction {
        initialValues.forEach { (id, name, value) ->
            if (!selectAll().where((this@SettingsTable.id eq id) and (this@SettingsTable.name eq name)).any()) {
                insert {
                    it[this.id] = EntityID(id, this)
                    it[this.name] = name
                    it[this.value] = value.toString()
                }
            }
        }
    }

    fun reset(): Unit = transaction {
        deleteAll()
        initialize()
    }

    internal companion object {
        const val AUTO_UPDATE = "autoUpdate"
        const val UPDATE_WARN = "updateWarn"
        const val CURSOR_ENABLED = "cursorEnabled"
        const val CURRENT_LANGUAGE = "current_language"
        const val CURSOR_COLOR = "cursor_color"
        const val PLAYER_FRAME_ENABLED = "PlayerFrame_enabled"
        const val DEFAULT_ELEMENT_VISIBILITY = "default_element_visibility"
        const val LABEL_STATE = "label_enabled"
        const val LABEL_COLOR = "label_color"
        const val CHANGELOGS_VERSION = "changelogs_version"
        const val SHOULD_OPEN_PLAYER_WINDOW_IN_FULL_SCREEN = "player_window_in_full_screen"
    }
}

internal data class SettingsInitialValue(val int: Int, val key: String, val value: Any)