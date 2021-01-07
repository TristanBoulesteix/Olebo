package jdr.exia.model.element

import jdr.exia.localization.Strings
import jdr.exia.model.dao.BlueprintTable
import jdr.exia.model.dao.InstanceTable
import jdr.exia.utils.CharacterException
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.io.File
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty

@Suppress("PropertyName")
class Blueprint(id: EntityID<Int>) : Entity<Int>(id) {
    companion object : EntityClass<Int, Blueprint>(BlueprintTable)

    var name by BlueprintTable.name
    var sprite by BlueprintTable.sprite
    private var maxLife by BlueprintTable.HP
    private var maxMana by BlueprintTable.MP
    var type by Type.TypeElement referencedOn BlueprintTable.idType

    var HP by statsDelegate(::maxLife)
    var MP by statsDelegate(::maxMana)

    val realName
        get() = if (type.typeElement == Type.BASIC) Strings[name] else name

    override fun delete() {
        File(sprite).delete()
        Element.find { InstanceTable.idBlueprint eq id.value }.forEach {
            it.delete()
        }
        super.delete()
    }

    private fun statsDelegate(stats: KMutableProperty0<Int?>) = object : ReadWriteProperty<Blueprint, Int> {
        override operator fun getValue(thisRef: Blueprint, property: KProperty<*>): Int =
            if (type.typeElement == Type.PNJ || type.typeElement == Type.PJ) stats.get()!! else throw Exception("Cet élément n'est pas un personnage !")

        override operator fun setValue(thisRef: Blueprint, property: KProperty<*>, value: Int) =
            if (type.typeElement == Type.PNJ || type.typeElement == Type.PJ) stats.set(value)
            else throw CharacterException(this::class, if (stats == ::maxLife) "HP" else "MP")
    }
}

