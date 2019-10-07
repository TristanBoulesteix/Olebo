package jdr.exia.model.act

import jdr.exia.model.dao.SceneTable
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.EntityID

//class Scene(val name: String, val background: ImageIcon, val elements: MutableList<Element>, var spawnPoint: Position = Position(0, 0))

class Scene(id: EntityID<Int>) : Entity<Int>(id) {
    companion object : EntityClass<Int, Scene>(SceneTable)

    val name by SceneTable.name
    val background by SceneTable.background
}