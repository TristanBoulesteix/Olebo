package jdr.exia.model.act

import jdr.exia.model.command.CommandManager
import jdr.exia.model.dao.InstanceTable
import jdr.exia.model.dao.SceneTable
import jdr.exia.model.element.Blueprint
import jdr.exia.model.element.Element
import jdr.exia.model.element.Elements
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.io.File

class Scene(id: EntityID<Int>) : Entity<Int>(id) {
    companion object : EntityClass<Int, Scene>(SceneTable) {
        /**
         * Move an element to a new scene
         *
         * @param element The element to move
         * @param scene The destination scene
         */
        fun moveElementToScene(element: Element, scene: Scene) {
            transaction {
                element.scene = scene
            }
        }
    }

    private val elementIterable by Element referrersOn InstanceTable.idScene

    val commandManager
        get() = CommandManager(id.value)

    val elements: Elements
        get() = transaction {
            Elements(elementIterable.filter { !it.isDeleted }.sortedBy { it.priority })
        }

    var name by SceneTable.name
    var background by SceneTable.background
    var idAct by SceneTable.idAct

    /**
     * Add an element to the scene as Instance
     *
     * @param blueprint The Blueprint to instanciate
     */
    fun addElement(blueprint: Blueprint) {
        val id = Element.createElement(blueprint).id
        transaction {
            InstanceTable.update({ InstanceTable.id eq id }) {
                it[idScene] = this@Scene.id.value
            }
        }
    }

    override fun delete() {
        File(background).delete()
        super.delete()
    }
}