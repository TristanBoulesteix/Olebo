package model.dao

import model.utils.toBoolean
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.sql.transactions.transaction

class Settings(id: EntityID<Int>) : IntEntity(id) {
    companion object : EntityClass<Int, Settings>(SettingsTable) {
        var databaseVersion
            get() = transaction(DAO.database) {
                this@Companion["baseVersion"]
                        ?: throw NullPointerException("Erreur de base de données ! Valeur manquante.")
            }
            set(value) {
                transaction(DAO.database) {
                    this@Companion["baseVersion"] = value
                }
            }

        var autoUpdate
            get() = transaction(DAO.database) { this@Companion["autoUpdate"] }.toBoolean()
            set(value) {
                transaction(DAO.database) {
                    this@Companion["autoUpdate"] = value.toString()
                }
            }

        var updateWarn
            get() = transaction(DAO.database) { this@Companion["updateWarn"] } ?: ""
            set(value) = transaction(DAO.database) {
                this@Companion["updateWarn"] = value
            }

        operator fun plusAssign(setting: Pair<String, Any?>) {
            this.new {
                this.name = setting.first
                this.value = setting.second?.toString() ?: ""
            }
        }

        operator fun get(setting: String) = this.find { SettingsTable.name eq setting }.firstOrNull()?.value

        operator fun set(setting: String, value: Any?) {
            this.find { SettingsTable.name eq setting }.firstOrNull()?.value = value?.toString() ?: ""
        }
    }

    private var name by SettingsTable.name
    private var value by SettingsTable.value
}