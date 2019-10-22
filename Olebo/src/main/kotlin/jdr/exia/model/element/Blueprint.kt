package jdr.exia.model.element

import jdr.exia.model.dao.BlueprintTable
import jdr.exia.model.dao.InstanceTable
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.EntityID
import java.io.File

@Suppress("PropertyName")
class Blueprint(id: EntityID<Int>) : Entity<Int>(id) {
    companion object : EntityClass<Int, Blueprint>(BlueprintTable)

    var name by BlueprintTable.name
    var sprite by BlueprintTable.sprite
    var HP by BlueprintTable.HP
    var MP by BlueprintTable.MP
    var type by Type.TypeElement referencedOn BlueprintTable.idType

    override fun delete() {
        File(sprite).delete()
        Element.find { InstanceTable.idBlueprint eq id.value }.forEach {
            it.delete()
        }
        super.delete()
    }
}

