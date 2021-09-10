package jdr.exia.model.act

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import jdr.exia.model.dao.ActTable
import jdr.exia.model.dao.SceneTable
import jdr.exia.model.type.Image
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.SizedIterable
import java.io.File
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

class Act(id: EntityID<Int>) : Entity<Int>(id) {
    companion object : EntityClass<Int, Act>(ActTable)

    var name by ActTable.name

    val scenes by Scene referrersOn SceneTable.idAct

    private var sceneId by ActTable.idScene

    override fun delete() {
        scenes.forEach {
            it.delete()
        }

        super.delete()
    }

    var currentScene
        get() = scenes.findWithId(sceneId) ?: scenes.first()
        set(value) {
            sceneId = value.id.value
        }


    /**
     * Find a scene with its id from all scenes stored in an Act
     */
    private fun SizedIterable<Scene>.findWithId(id: Int) = this.find { it.id.value == id }

    /**
     * Temporary scene
     */
    @Immutable
    data class SceneData(val name: String, @Stable val img: Image, @Stable val id: EntityID<Int>? = null) {
        companion object {
            fun default() = SceneData("", Image.unspecified)
        }
    }
}

/**
 * Check if the receiver [Act.SceneData] has the same id as the parameter of the method.
 * This function can be infix if the smartcast isn't required.
 */
@OptIn(ExperimentalContracts::class)
@Stable
infix fun Act.SceneData?.isValidAndEqualTo(sceneData: Act.SceneData?): Boolean {
    contract {
        returns(true) implies (sceneData != null && this@isValidAndEqualTo != null)
    }

    return this.isValid() && (this == sceneData || (sceneData.isValid() && sceneData.id == this.id))
}

@OptIn(ExperimentalContracts::class)
fun Act.SceneData?.isValid(): Boolean {
    contract {
        returns(true) implies (this@isValid != null)
    }

    return this != null && this.name.isNotBlank() && this.img.isValid() && File(this.img.path)
        .let { it.exists() && it.isFile }
}