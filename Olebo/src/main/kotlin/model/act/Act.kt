package model.act

import model.dao.ActTable
import model.dao.DAO
import model.dao.SceneTable
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.transactions.transaction

class Act(id: EntityID<Int>) : Entity<Int>(id) {
    companion object : EntityClass<Int, Act>(ActTable)

    private val scenesIterable by Scene referrersOn SceneTable.idAct

    var name by ActTable.name
    val scenes
        get() = transaction(DAO.database) { scenesIterable.toMutableList() }
    var sceneId by ActTable.idScene

    override fun delete() {
        scenesIterable.forEach {
            it.delete()
        }

        super.delete()
    }

    /**
     * Find a scene with its id from all scenes stored in an Act
     */
    fun MutableList<Scene>.findWithId(id: Int): Scene? {
        return this.find { it.id.value == id }
    }
}