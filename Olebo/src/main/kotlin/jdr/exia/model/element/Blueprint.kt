package jdr.exia.model.element

import androidx.compose.runtime.Immutable
import jdr.exia.localization.StringLocale
import jdr.exia.model.dao.BlueprintTable
import jdr.exia.model.dao.InstanceTable
import jdr.exia.model.type.Image
import jdr.exia.model.tools.isCharacter
import jdr.exia.utils.CharacterException
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty

@Suppress("PropertyName")
class Blueprint(id: EntityID<Int>) : Entity<Int>(id) {
    companion object : EntityClass<Int, Blueprint>(BlueprintTable)

    var name by BlueprintTable.name
    private var _sprite by BlueprintTable.sprite
    private var maxLife by BlueprintTable.HP
    private var maxMana by BlueprintTable.MP
    private var _type by Type.TypeElement referencedOn BlueprintTable.idType

    var HP by statsDelegate(::maxLife)
    var MP by statsDelegate(::maxMana)

    var type
        get() = _type.typeElement
        set(value) {
            _type = value.type
        }

    var sprite
        get() = _sprite
        set(value) {
            val oldImg = if (id._value != null) File(_sprite).takeIf { it.exists() } else null
            _sprite = value
            oldImg?.delete()
        }

    /**
     * Name as it has to be displayed to the user
     */
    val realName
        get() = if (type == Type.BASIC) StringLocale[name] else name

    override fun delete() {
        File(sprite).delete()
        Element.find { InstanceTable.idBlueprint eq id.value }.forEach {
            it.delete()
        }
        super.delete()
    }

    private fun getLifeOrNull() = if (isCharacter()) HP else null

    private fun getManaOrNull() = if (isCharacter()) MP else null

    fun toBlueprintData() = transaction {
        BlueprintData(
            name,
            Image(sprite),
            getLifeOrNull(),
            getManaOrNull(),
            type,
            this@Blueprint.id
        )
    }

    private fun statsDelegate(stats: KMutableProperty0<Int?>) = object : ReadWriteProperty<Blueprint, Int> {
        override operator fun getValue(thisRef: Blueprint, property: KProperty<*>): Int =
            if (type == Type.PNJ || type == Type.PJ) stats.get()!! else throw Exception("Cet élément n'est pas un personnage !")

        override operator fun setValue(thisRef: Blueprint, property: KProperty<*>, value: Int) =
            if (type == Type.PNJ || type == Type.PJ) stats.set(value)
            else throw CharacterException(this::class, if (stats == ::maxLife) "HP" else "MP")
    }

    /**
     * Temporary [Blueprint] for objects
     */
    @Immutable
    data class BlueprintData(
        val name: String,
        val img: Image,
        val life: Int? = null,
        val mana: Int? = null,
        val type: Type = Type.OBJECT,
        val id: EntityID<Int>? = null
    ) {
        companion object {
            fun defaultObject() = BlueprintData("", Image.unspecified)

            fun defaultCharacter(type: Type) = BlueprintData("", Image.unspecified, 0, 0, type)
        }
    }
}

@OptIn(ExperimentalContracts::class)
fun Blueprint.BlueprintData?.isValid(): Boolean {
    contract {
        returns(true) implies (this@isValid != null)
    }

    return this != null
            && this.img.isValid()
            && File(this.img.path).let { it.exists() && it.isFile }
}
