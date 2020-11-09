package model.element

import utils.CharacterException
import model.dao.BlueprintTable
import model.dao.InstanceTable
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.EntityID
import java.io.File

@Suppress("PropertyName")
class Blueprint(id: EntityID<Int>) : Entity<Int>(id) {
    companion object : EntityClass<Int, Blueprint>(BlueprintTable)

    var name by BlueprintTable.name
    var sprite by BlueprintTable.sprite
    private var maxLife by BlueprintTable.HP
    private var maxMana by BlueprintTable.MP
    var type by Type.TypeElement referencedOn BlueprintTable.idType

    var HP
        get() = if (type.typeElement == Type.PNJ || type.typeElement == Type.PJ) maxLife!! else throw Exception("Cet élément n'est pas un personnage !")
        set(value) = if (type.typeElement == Type.PNJ || type.typeElement == Type.PJ) maxLife = value
        else throw CharacterException(this::class, "HP")

    var MP
        get() = if (type.typeElement == Type.PNJ || type.typeElement == Type.PJ) maxMana!! else throw Exception("Cet élément n'est pas un personnage !")
        set(value) = if (type.typeElement == Type.PNJ || type.typeElement == Type.PJ) maxMana = value
        else throw CharacterException(this::class, "MP")

    override fun delete() {
        File(sprite).delete()
        Element.find { InstanceTable.idBlueprint eq id.value }.forEach {
            it.delete()
        }
        super.delete()
    }
}

