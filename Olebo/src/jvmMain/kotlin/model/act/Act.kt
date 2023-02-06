package jdr.exia.model.act

import jdr.exia.model.dao.ActTable
import jdr.exia.model.dao.ActTagTable
import jdr.exia.model.dao.SceneTable
import jdr.exia.model.element.Tag
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID

class Act(id: EntityID<Int>) : Entity<Int>(id) {
    companion object : EntityClass<Int, Act>(ActTable)

    var name by ActTable.name

    val scenes by Scene referrersOn SceneTable.idAct

    var currentScene by Scene referencedOn ActTable.scene

    var tags by Tag via ActTagTable

    override fun delete() {
        scenes.forEach {
            it.delete()
        }

        super.delete()
    }

    override fun equals(other: Any?): Boolean = other is Act && id == other.id

    override fun hashCode(): Int = id.hashCode()
}