package jdr.exia.model.dao

import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

object ActTable : IntIdTable() {
    val name = varchar("name", 50)
    val idScene = integer("id_scene").references(SceneTable.id).default(0)
}

object SceneTable : IntIdTable() {
    val name = varchar("name", 50)
    val background = varchar("background", 200)
    val idAct = integer("id_act").references(ActTable.id)
}

object BlueprintTable : IntIdTable() {
    val name = varchar("name", 50)
    val sprite = varchar("sprite", 200)
    val HP = integer("HP")
    val MP = integer("MP")
    val idType = reference("id_type", TypeTable)
}

object TypeTable : IntIdTable() {
    val name = varchar("type", 50)
}

object InstanceTable : IntIdTable() {
    val currentHP = integer("current_HP")
    val currentMP = integer("current_MP")
    val x = integer("x")
    val y = integer("y")
    val size = varchar("Size", 10)
    val visible = integer("Visible")
    val idScene = integer("ID_Scene").references(SceneTable.id)
    val idBlueprint = integer("id_blueprint").references(BlueprintTable.id)

    val nextId
        get() = this.slice(size).selectAll()
}

fun main() {
    transaction(DAO.database) {
        InstanceTable.nextId.forEach {
            println(it[InstanceTable.size])
        }
    }
}