package jdr.exia.model.dao.option

import jdr.exia.localization.ST_UNKNOWN_DATABASE_VERSION
import jdr.exia.localization.Strings
import jdr.exia.model.dao.DAO
import jdr.exia.model.dao.SettingsTable
import jdr.exia.model.dao.SettingsTable.AUTO_UPDATE
import jdr.exia.model.dao.SettingsTable.BASE_VERSION
import jdr.exia.model.dao.SettingsTable.CURRENT_LANGUAGE
import jdr.exia.model.dao.SettingsTable.CURSOR_COLOR
import jdr.exia.model.dao.SettingsTable.CURSOR_ENABLED
import jdr.exia.model.dao.SettingsTable.DEFAULT_ELEMENT_VISIBILITY
import jdr.exia.model.dao.SettingsTable.LABEL_ENABLED
import jdr.exia.model.dao.SettingsTable.PLAYER_FRAME_ENABLED
import jdr.exia.model.dao.SettingsTable.UPDATE_WARN
import jdr.exia.model.utils.toBoolean
import jdr.exia.utils.MessageException
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

class Settings(id: EntityID<Int>) : IntEntity(id) {
    companion object : EntityClass<Int, Settings>(SettingsTable) {
        var databaseVersion
            get() = transaction(DAO.database) {
                this@Companion[BASE_VERSION]?.toIntOrNull()
                    ?: throw MessageException(Strings[ST_UNKNOWN_DATABASE_VERSION])
            }
            set(value) {
                transaction(DAO.database) {
                    this@Companion[BASE_VERSION] = value
                }
            }

        var autoUpdate
            get() = transaction(DAO.database) { this@Companion[AUTO_UPDATE] }.toBoolean()
            set(value) {
                transaction(DAO.database) {
                    this@Companion[AUTO_UPDATE] = value
                }
            }

        var updateWarn
            get() = transaction(DAO.database) { this@Companion[UPDATE_WARN] } ?: ""
            set(value) = transaction(DAO.database) {
                this@Companion[UPDATE_WARN] = value
            }

        var cursorEnabled
            get() = transaction(DAO.database) { this@Companion[CURSOR_ENABLED].toBoolean() }
            set(value) = transaction(DAO.database) {
                this@Companion[CURSOR_ENABLED] = value
            }

        var language: Locale
            get() = try {
                transaction(DAO.database) { Locale(this@Companion[CURRENT_LANGUAGE]) }
            } catch (e: Exception) {
                Locale.getDefault()
            }
            set(value) = transaction(DAO.database) {
                this@Companion[CURRENT_LANGUAGE] = value.language
            }

        val activeLanguage by lazy { language }

        var cursorColor
            get() = transaction(DAO.database) {
                CursorColor[this@Companion[CURSOR_COLOR]!!]
            }
            set(value) = transaction(DAO.database) {
                this@Companion[CURSOR_COLOR] = value.encode()
            }

        var playerFrameOpenedByDefault
            get() = transaction(DAO.database) { this@Companion[PLAYER_FRAME_ENABLED].toBoolean() }
            set(value) = transaction(DAO.database) {
                this@Companion[PLAYER_FRAME_ENABLED] = value
            }

        var defaultElementVisibility
            get() = transaction(DAO.database) { this@Companion[DEFAULT_ELEMENT_VISIBILITY].toBoolean() }
            set(value) = transaction(DAO.database) {
                this@Companion[DEFAULT_ELEMENT_VISIBILITY] = value
            }

        var isLabelEnabled
            get() = transaction(DAO.database) { this@Companion[LABEL_ENABLED].toBoolean() }
            set(value) = transaction(DAO.database) {
                this@Companion[LABEL_ENABLED] = value
            }

        operator fun get(setting: String) = this.find { SettingsTable.name eq setting }.firstOrNull()?.value

        operator fun set(setting: String, value: Any?) {
            this.find { SettingsTable.name eq setting }.firstOrNull()?.value = value?.toString() ?: ""
        }
    }

    private var value by SettingsTable.value
}