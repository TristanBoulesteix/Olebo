package model.act

import model.dao.DAO
import model.dao.InstanceTable
import model.dao.SceneTable
import model.element.Blueprint
import model.element.Element
import model.utils.DelegateIterable
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.io.File

class Scene(id: EntityID<Int>) : Entity<Int>(id) {
    companion object : EntityClass<Int, Scene>(SceneTable) {
        override fun new(init: Scene.() -> Unit): Scene {
            /**
             * Check foreign key before insert
             */
            fun Array<Pair<String, String>>.checkContent(toCheck: String): Boolean {
                this.forEach {
                    if (it.first == toCheck) return true
                }

                return false
            }

            val newScene = super.new(init)

            return if (DAO.getActsList().checkContent(newScene.idAct.toString())) newScene
            else throw Exception("Error ! Invalid foreign key")
        }

        /**
         * Move an element to a new scene
         *
         * @param element The element to move
         * @param scene The destination scene
         */
        fun moveElementToScene(element: Element, scene: Scene) {
            transaction(DAO.database) {
                element.scene = scene
            }
        }
    }

    private val elementIterable by Element referrersOn InstanceTable.idScene

    val elements by DelegateIterable { elementIterable }
    var name by SceneTable.name
    var background by SceneTable.background
    var idAct by SceneTable.idAct

    /**
     * Add an element to the scene as Instance
     *
     * @param element The Blueprint to instanciate
     */
    fun addElement(element: Blueprint) {
        val id = Element.createElement(element).id
        transaction {
            InstanceTable.update({ InstanceTable.id eq id }) {
                it[idScene] = this@Scene.id.value
            }
        }
    }

    /**
     * Move all elements from the current scene to a new one
     *
     * @param scene The new scene
     */
    fun moveElementsToScene(scene: Scene) {
        transaction(DAO.database) {
            elementIterable.forEach {
                it.scene = scene
            }
        }
    }

    override fun delete() {
        File(background).delete()
        super.delete()
    }
}