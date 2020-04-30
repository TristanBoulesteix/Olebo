package jdr.exia.model.dao

import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import jdr.exia.model.utils.toBoolean

class Settings(id: EntityID<Int>) : IntEntity(id) {
    companion object : EntityClass<Int, Settings>(SettingsTable) {
        var databaseVersion
            get() = this["baseVersion"] ?: throw NullPointerException("Erreur de base de données ! Valeur manquante.")
            set(value) {
                this["baseVersion"] = value
            }

        var autoUpdate
            get() = this["autoUpdate"].toBoolean()
            set(value) {
                this["autoUpdate"] = value.toString()
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