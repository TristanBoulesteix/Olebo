package jdr.exia.model.dao.option

import jdr.exia.OLEBO_VERSION_CODE
import jdr.exia.localization.Language
import jdr.exia.localization.languageCode
import jdr.exia.model.dao.DAO
import jdr.exia.model.dao.SettingsTable
import jdr.exia.model.dao.SettingsTable.AUTO_UPDATE
import jdr.exia.model.dao.SettingsTable.CHANGELOGS_VERSION
import jdr.exia.model.dao.SettingsTable.CURRENT_LANGUAGE
import jdr.exia.model.dao.SettingsTable.CURSOR_COLOR
import jdr.exia.model.dao.SettingsTable.CURSOR_ENABLED
import jdr.exia.model.dao.SettingsTable.DEFAULT_ELEMENT_VISIBILITY
import jdr.exia.model.dao.SettingsTable.LABEL_COLOR
import jdr.exia.model.dao.SettingsTable.LABEL_STATE
import jdr.exia.model.dao.SettingsTable.PLAYER_FRAME_ENABLED
import jdr.exia.model.dao.SettingsTable.UPDATE_WARN
import jdr.exia.model.tools.toBoolean
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

object Settings {
    var autoUpdate
        get() = transaction(DAO.database) { this@Settings[AUTO_UPDATE] }.toBoolean()
        set(value) {
            transaction(DAO.database) {
                this@Settings[AUTO_UPDATE] = value
            }
        }

    var updateWarn
        get() = transaction(DAO.database) { this@Settings[UPDATE_WARN] } ?: ""
        set(value) = transaction(DAO.database) {
            this@Settings[UPDATE_WARN] = value
        }

    var cursorEnabled
        get() = transaction(DAO.database) { this@Settings[CURSOR_ENABLED].toBoolean() }
        set(value) = transaction(DAO.database) {
            this@Settings[CURSOR_ENABLED] = value
        }

    var language: Language
        get() = try {
            transaction(DAO.database) { Language(this@Settings[CURRENT_LANGUAGE].orEmpty()) }
        } catch (e: Exception) {
            Language.getDefault()
        }
        set(value) = transaction(DAO.database) {
            this@Settings[CURRENT_LANGUAGE] = value.languageCode
        }

    val activeLanguage by lazy { language }

    var cursorColor
        get() = transaction(DAO.database) {
            SerializableColor[this@Settings[CURSOR_COLOR]!!]
        }
        set(value) = transaction(DAO.database) {
            this@Settings[CURSOR_COLOR] = value.encode()
        }

    var playerFrameOpenedByDefault
        get() = transaction(DAO.database) { this@Settings[PLAYER_FRAME_ENABLED].toBoolean() }
        set(value) = transaction(DAO.database) {
            this@Settings[PLAYER_FRAME_ENABLED] = value
        }

    var defaultElementVisibility
        get() = transaction(DAO.database) { this@Settings[DEFAULT_ELEMENT_VISIBILITY].toBoolean() }
        set(value) = transaction(DAO.database) {
            this@Settings[DEFAULT_ELEMENT_VISIBILITY] = value
        }

    var labelState
        get() = transaction(DAO.database) { SerializableLabelState[this@Settings[LABEL_STATE]!!] }
        set(value) = transaction(DAO.database) {
            this@Settings[LABEL_STATE] = value.encode()
        }

    var labelColor
        get() = transaction(DAO.database) {
            SerializableColor[this@Settings[LABEL_COLOR]!!]
        }
        set(value) = transaction(DAO.database) {
            this@Settings[LABEL_COLOR] = value.encode()
        }

    var wasJustUpdated
        get() = transaction(DAO.database) {
            this@Settings[CHANGELOGS_VERSION] == OLEBO_VERSION_CODE.toString()
        }
        set(value) = transaction(DAO.database) {
            this@Settings[CHANGELOGS_VERSION] = if (value) OLEBO_VERSION_CODE else null
        }

    operator fun get(setting: String) =
        SettingsTable.select { SettingsTable.name eq setting }.firstOrNull()?.get(SettingsTable.value)

    operator fun set(setting: String, value: Any?) {
        SettingsTable.update(
            where = { SettingsTable.name eq setting },
            body = { it[SettingsTable.value] = value?.toString() ?: "" }
        )
    }
}