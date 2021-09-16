package jdr.exia.model.act

import jdr.exia.localization.STR_NEW_ELEMENT
import jdr.exia.localization.StringLocale
import jdr.exia.model.command.Command
import jdr.exia.model.command.CommandManager
import jdr.exia.model.dao.InstanceTable
import jdr.exia.model.dao.SceneTable
import jdr.exia.model.element.Blueprint
import jdr.exia.model.element.Element
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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
        fun moveElementToScene(scene: Scene, element: List<Element>) {
            transaction {
                element.forEach {
                    it.scene = scene
                }
            }
        }
    }

    private val elementIterable by Element referrersOn InstanceTable.idScene

    val commandManager
        get() = CommandManager(id)

    val elements
        get() = transaction { elementIterable.filter { !it.isDeleted } }

    var name by SceneTable.name
    var background by SceneTable.background
    var idAct by SceneTable.idAct

    /**
     * Add an element to the scene as Instance
     *
     * @param blueprint The Blueprint to instantiate
     */
    suspend fun addElement(blueprint: Blueprint): Element = withContext(Dispatchers.IO) {
        val element = Element.createElement(blueprint)

        commandManager += object : Command {
            override val label = StringLocale[STR_NEW_ELEMENT]

            override fun exec(): Unit = transaction {
                InstanceTable.update({ InstanceTable.id eq element.id }) {
                    it[idScene] = this@Scene.id.value
                    it[deleted] = false
                }
            }

            override fun cancelExec(): Unit = transaction {
                InstanceTable.update({ InstanceTable.id eq element.id }) {
                    it[deleted] = true
                }
            }
        }

        return@withContext element
    }

    override fun delete() {
        File(background).delete()
        super.delete()
    }
}