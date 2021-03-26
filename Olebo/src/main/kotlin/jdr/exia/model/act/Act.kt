package jdr.exia.model.act

import jdr.exia.model.dao.ActTable
import jdr.exia.model.dao.SceneTable
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.SizedIterable

class Act(id: EntityID<Int>) : Entity<Int>(id) {
    companion object : EntityClass<Int, Act>(ActTable)

    var name by ActTable.name

    val scenes by Scene referrersOn SceneTable.idAct

    var sceneId by ActTable.idScene

    override fun delete() {
        scenes.forEach {
            it.delete()
        }

        super.delete()
    }

    /**
     * Find a scene with its id from all scenes stored in an Act
     */
    fun SizedIterable<Scene>.findWithId(id: Int): Scene? {
        return this.find { it.id.value == id }
    }
}
