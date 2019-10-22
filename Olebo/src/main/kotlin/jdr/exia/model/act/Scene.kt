package jdr.exia.model.act

import jdr.exia.model.dao.DAO
import jdr.exia.model.dao.InstanceTable
import jdr.exia.model.dao.SceneTable
import jdr.exia.model.element.Element
import jdr.exia.model.utils.DelegateIterable
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.EntityID
import java.io.File

class Scene(id: EntityID<Int>) : Entity<Int>(id) {
    companion object : EntityClass<Int, Scene>(SceneTable) {
        override fun new(init: Scene.() -> Unit): Scene {
            fun Array<Pair<String, String>>.checkContent(toCheck: String): Boolean {
                var contains = false

                this.forEach {
                    if (it.first == toCheck) contains = true
                }

                return contains
            }

            val newScene = super.new(init)

            return if (DAO.getActsList().checkContent(newScene.idAct.toString())) newScene
            else throw Exception("Error ! Invalid foreign key")
        }
    }

    private val elementIterable by Element referrersOn InstanceTable.idScene

    val elements by DelegateIterable { elementIterable }
    var name by SceneTable.name
    var background by SceneTable.background
    var idAct by SceneTable.idAct

    override fun delete() {
        File(background).delete()
        super.delete()
    }
}