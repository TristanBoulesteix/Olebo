package jdr.exia.model.element

import jdr.exia.model.dao.BlueprintTable
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.EntityID

@Suppress("PropertyName")
class Blueprint(id: EntityID<Int>) : Entity<Int>(id) {
    companion object : EntityClass<Int, Blueprint>(BlueprintTable)

    var name by BlueprintTable.name
    var sprite by BlueprintTable.sprite
    var HP by BlueprintTable.HP
    var MP by BlueprintTable.MP
    var type by Type.TypeElement referencedOn BlueprintTable.idType
    var size by BlueprintTable.size
}

