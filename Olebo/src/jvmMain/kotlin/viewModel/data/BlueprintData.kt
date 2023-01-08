package jdr.exia.viewModel.data

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import jdr.exia.model.act.Act
import jdr.exia.model.element.Blueprint
import jdr.exia.model.element.TypeElement
import jdr.exia.model.tools.getLifeOrNull
import jdr.exia.model.tools.getManaOrNull
import jdr.exia.model.tools.isFileValid
import jdr.exia.model.type.Image
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/**
 * Temporary [Blueprint] for objects
 */
@Immutable
data class BlueprintData(
    val name: String,
    val img: Image,
    val life: Int? = null,
    val mana: Int? = null,
    val type: TypeElement = TypeElement.Object,
    val tags: List<String> = emptyList(),
    val associatedActs: List<Act> = emptyList(),
    val id: EntityID<Int>? = null
) {
    companion object {
        fun defaultObject(associatedActs: List<Act> = emptyList()) = BlueprintData("", Image.unspecified, associatedActs = associatedActs)

        fun defaultCharacter(type: TypeElement, associatedActs: List<Act> = emptyList()) = BlueprintData("", Image.unspecified, 0, 0, type, associatedActs = associatedActs)
    }
}

@OptIn(ExperimentalContracts::class)
fun BlueprintData?.isValid(): Boolean {
    contract {
        returns(true) implies (this@isValid != null)
    }

    return this != null
            && !this.img.isUnspecified()
            && this.img.checkedImgPath?.toFile().isFileValid()
}

fun Blueprint.toBlueprintData() = transaction {
    BlueprintData(
        name,
        Image(sprite),
        getLifeOrNull(),
        getManaOrNull(),
        type,
        tags.map { it.value },
        associatedAct.toList(),
        this@toBlueprintData.id
    )
}

@Stable
fun BlueprintData.isCharacter() = type in setOf(TypeElement.PJ, TypeElement.PNJ)

fun BlueprintData.isDefault() =
    id == null && (this.copy(associatedActs = emptyList()) == if (type == TypeElement.Object)
        BlueprintData.defaultObject()
    else
        BlueprintData.defaultCharacter(type))