package jdr.exia.model.act

import jdr.exia.localization.StringDelegate
import jdr.exia.model.command.Command
import jdr.exia.model.command.CommandManager
import jdr.exia.model.dao.InstanceTable
import jdr.exia.model.dao.SceneTable
import jdr.exia.model.element.Blueprint
import jdr.exia.model.element.Element
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
        get() = CommandManager(id)

    val elements
        get() = transaction {
            elementIterable.filter { !it.isDeleted }.sortedBy { it.priority }
        }

    var name by SceneTable.name
    var background by SceneTable.background
    var idAct by SceneTable.idAct

    /**
     * Add an element to the scene as Instance
     *
     * @param blueprint The Blueprint to instantiate
     */
    fun addElement(blueprint: Blueprint) {
        val id = Element.createElement(blueprint).id

        commandManager += object : Command {
            override val label by StringDelegate("Créer un élément")

            override fun exec() {
                transaction {
                    InstanceTable.update({ InstanceTable.id eq id }) {
                        it[idScene] = this@Scene.id.value
                        it[deleted] = false
                    }
                }
            }

            override fun cancelExec() {
                transaction {
                    InstanceTable.update({ InstanceTable.id eq id }) {
                        it[deleted] = true
                    }
                }
            }

        }
    }

    override fun delete() {
        File(background).delete()
        super.delete()
    }
}