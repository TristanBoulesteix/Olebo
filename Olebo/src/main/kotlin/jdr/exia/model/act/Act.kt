package jdr.exia.model.act

import jdr.exia.model.dao.ActTable
import jdr.exia.model.dao.SceneTable
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.SizedIterable
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

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
    fun SizedIterable<Scene>.findWithId(id: Int) = this.find { it.id.value == id }

    /**
     * Temporary scene
     */
    data class SceneData(val name: String, val img: String, val id: Int? = null) {
        companion object {
            fun default() = SceneData("", "")
        }
    }
}

/**
 * Check if the receiver [Act.SceneData] has the same id as the parameter of the method.
 * This function can be infix if the smartcast isn't required.
 */
@OptIn(ExperimentalContracts::class)
infix fun Act.SceneData?.isValidAndEqualTo(sceneData: Act.SceneData?): Boolean {
    contract {
        returns(true) implies (sceneData != null && this@isValidAndEqualTo != null)
    }

    return this?.let { sceneData != null && sceneData.id == it.id } ?: false
}
