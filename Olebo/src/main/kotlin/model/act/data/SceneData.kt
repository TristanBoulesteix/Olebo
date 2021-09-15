package jdr.exia.model.act.data

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import jdr.exia.model.type.Image
import org.jetbrains.exposed.dao.id.EntityID
import java.io.File
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/**
 * Temporary scene
 */
@Immutable
data class SceneData(val name: String, @Stable val img: Image, @Stable val id: EntityID<Int>? = null) {
    companion object {
        fun default() = SceneData("", Image.unspecified)
    }
}

/**
 * Check if the receiver [SceneData] has the same id as the parameter of the method.
 * This function can be infix if the smartcast isn't required.
 */
@OptIn(ExperimentalContracts::class)
@Stable
infix fun SceneData?.isValidAndEqualTo(sceneData: SceneData?): Boolean {
    contract {
        returns(true) implies (sceneData != null && this@isValidAndEqualTo != null)
    }

    return this.isValid() && (this == sceneData || (sceneData.isValid() && sceneData.id == this.id))
}

@OptIn(ExperimentalContracts::class)
fun SceneData?.isValid(): Boolean {
    contract {
        returns(true) implies (this@isValid != null)
    }

    return this != null && this.name.isNotBlank() && this.img.isValid() && File(this.img.path)
        .let { it.exists() && it.isFile }
}