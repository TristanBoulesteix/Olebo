package jdr.exia.model.dao

import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity

class Settings(id: EntityID<Int>) : IntEntity(id) {
    companion object : EntityClass<Int, Settings>(SettingsTable) {
        operator fun plusAssign(setting: Pair<String, Any?>) {
            this.new {
                this.name = setting.first
                this.value = setting.second.toString()
            }
        }

        operator fun get(setting: String) = this.find { SettingsTable.name eq setting }.firstOrNull()
    }

    var value by SettingsTable.value
    var name by SettingsTable.name
}