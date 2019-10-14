package jdr.exia.model.act

import jdr.exia.model.dao.ActTable
import jdr.exia.model.dao.SceneTable
import jdr.exia.model.utils.DelegateIterable
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.EntityID

class Act(id: EntityID<Int>) : Entity<Int>(id) {
    companion object : EntityClass<Int, Act>(ActTable)

    private val scenesIterable by Scene referrersOn SceneTable.idAct

    var name by ActTable.name
    val scenes by DelegateIterable { scenesIterable }
    var sceneId by ActTable.idScene

    override fun delete() {
        scenesIterable.forEach {
            it.delete()
        }

        super.delete()
    }

    fun MutableList<Scene>.findWithId(id: Int): Scene? {
        return this.find { it.id.value == id }
    }
}
