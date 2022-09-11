package jdr.exia.model.element

import jdr.exia.localization.StringLocale
import jdr.exia.localization.get
import jdr.exia.model.dao.BlueprintTable
import jdr.exia.model.dao.BlueprintTagTable
import jdr.exia.model.dao.InstanceTable
import jdr.exia.model.tools.CharacterException
import jdr.exia.model.type.checkedImgPath
import jdr.exia.model.type.toImgPath
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.io.File
import kotlin.io.path.deleteIfExists
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
    private var typeEntity by TypeElement.TypeEntity referencedOn BlueprintTable.idType

    var tags by Tag via BlueprintTagTable

    var HP by statsDelegate(::maxLife)
    var MP by statsDelegate(::maxMana)

    var type
        get() = typeEntity.type
        set(value) {
            typeEntity = TypeElement.TypeEntity[value]
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
        get() = if (type == TypeElement.Basic) StringLocale[name] else name

    override fun delete() {
        sprite.toImgPath().checkedImgPath()?.deleteIfExists()
        Element.find { InstanceTable.idBlueprint eq id.value }.forEach {
            it.delete()
        }
        super.delete()
    }

    private fun statsDelegate(stats: KMutableProperty0<Int?>) = object : ReadWriteProperty<Blueprint, Int> {
        override operator fun getValue(thisRef: Blueprint, property: KProperty<*>): Int =
            if (type == TypeElement.PNJ || type == TypeElement.PJ) stats.get()!! else throw Exception("Cet élément n'est pas un personnage !")

        override operator fun setValue(thisRef: Blueprint, property: KProperty<*>, value: Int) =
            if (type == TypeElement.PNJ || type == TypeElement.PJ) stats.set(value)
            else throw CharacterException(this::class, if (stats == ::maxLife) "HP" else "MP")
    }
}
