package model.dao

import model.dao.SettingsTable.AUTO_UPDATE
import model.dao.SettingsTable.BASE_VERSION
import model.dao.SettingsTable.CURSOR_ENABLED
import model.dao.SettingsTable.UPDATE_WARN
import model.utils.toBoolean
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.transactions.transaction

class Settings(id: EntityID<Int>) : IntEntity(id) {
    companion object : EntityClass<Int, Settings>(SettingsTable) {
        var databaseVersion
            get() = transaction(DAO.database) {
                this@Companion[BASE_VERSION]?.toIntOrNull()
                        ?: throw NullPointerException("Erreur de base de données ! Valeur manquante.")
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

        operator fun get(setting: String) = this.find { SettingsTable.name eq setting }.firstOrNull()?.value

        operator fun set(setting: String, value: Any?) {
            this.find { SettingsTable.name eq setting }.firstOrNull()?.value = value?.toString() ?: ""
        }
    }

    private var value by SettingsTable.value
}