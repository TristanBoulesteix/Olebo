package fr.olebo.persistence.tables

import fr.olebo.domain.Constants
import fr.olebo.domain.models.Configurations
import fr.olebo.domain.models.get
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

internal object SettingsTable : IntIdTable(), Initializable {
    internal const val AUTO_UPDATE = "autoUpdate"
    internal const val UPDATE_WARN = "updateWarn"
    internal const val CURSOR_ENABLED = "cursorEnabled"
    internal const val CURRENT_LANGUAGE = "current_language"
    internal const val CURSOR_COLOR = "cursor_color"
    internal const val PLAYER_FRAME_ENABLED = "PlayerFrame_enabled"
    internal const val DEFAULT_ELEMENT_VISIBILITY = "default_element_visibility"
    internal const val LABEL_STATE = "label_enabled"
    internal const val LABEL_COLOR = "label_color"
    internal const val CHANGELOGS_VERSION = "changelogs_version"
    internal const val SHOULD_OPEN_PLAYER_WINDOW_IN_FULL_SCREEN = "player_window_in_full_screen"

    val name = varchar("name", 255)

    val value = varchar("value", 255).default("")

    fun getInitialValues(configurations: Configurations): List<SettingsInitialValue> {
        val constants = configurations.get<Constants>()

        return listOf(
            SettingsInitialValue(2, AUTO_UPDATE, true),
            SettingsInitialValue(3, UPDATE_WARN, ""),
            SettingsInitialValue(4, CURSOR_ENABLED, true),
            SettingsInitialValue(5, CURRENT_LANGUAGE, Locale.getDefault().language),
            SettingsInitialValue(6, CURSOR_COLOR, ""),
            SettingsInitialValue(7, PLAYER_FRAME_ENABLED, false),
            SettingsInitialValue(8, DEFAULT_ELEMENT_VISIBILITY, false),
            SettingsInitialValue(9, LABEL_STATE, constants.defaultLabelVisibility),
            SettingsInitialValue(10, LABEL_COLOR, constants.defaultLabelColor),
            SettingsInitialValue(11, CHANGELOGS_VERSION, ""),
            SettingsInitialValue(12, SHOULD_OPEN_PLAYER_WINDOW_IN_FULL_SCREEN, true),
        )
    }

    override fun initialize(configurations: Configurations) = transaction {
        getInitialValues(configurations).forEach { (id, name, value) ->
            if (!selectAll().where((this@SettingsTable.id eq id) and (this@SettingsTable.name eq name)).any()) {
                insert {
                    it[this.id] = EntityID(id, this)
                    it[this.name] = name
                    it[this.value] = value.toString()
                }
            }
        }
    }

    fun reset(configurations: Configurations): Unit = transaction {
        deleteAll()
        initialize(configurations)
    }

    internal data class SettingsInitialValue(val int: Int, val key: String, val value: Any)
}