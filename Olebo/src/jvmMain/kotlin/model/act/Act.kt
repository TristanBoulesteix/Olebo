package jdr.exia.model.act

import jdr.exia.model.dao.*
import jdr.exia.model.element.Blueprint
import jdr.exia.model.element.Tag
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

class Act(id: EntityID<Int>) : Entity<Int>(id) {
    companion object : EntityClass<Int, Act>(ActTable)

    var name by ActTable.name

    val scenes by Scene referrersOn SceneTable.idAct

    var currentScene by Scene referencedOn ActTable.scene

    var tags by Tag via ActTagTable

    val associatedBlueprints
        get() = transaction {
            val query = BlueprintTable.leftJoin(BlueprintTagTable).select {
                BlueprintTagTable.tag inList tags.map { it.value }
            }.withDistinct()

            Blueprint.wrapRows(query).distinct()
        }

    override fun delete() {
        scenes.forEach {
            it.delete()
        }

        super.delete()
    }
}