package jdr.exia.model.dao

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IdTable
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select

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
    val name = varchar("name", 50).uniqueIndex()
    val sprite = varchar("sprite", 200)
    val HP = integer("HP").nullable()
    val MP = integer("MP").nullable()
    val idType = reference("id_type", TypeTable)
}

object TypeTable : IntIdTable() {
    val name = varchar("type", 50)

    fun initialize() {
        if (TypeTable.select((id eq 1) and (name eq "Object")).count() <= 0) {
            TypeTable.insert {
                it[id] = EntityID(1, TypeTable)
                it[name] = "Object"
            }
        }

        if (TypeTable.select((id eq 2) and (name eq "PJ")).count() <= 0) {
            TypeTable.insert {
                it[id] = EntityID(2, TypeTable)
                it[name] = "PJ"
            }
        }

        if (TypeTable.select((id eq 3) and (name eq "PNJ")).count() <= 0) {
            TypeTable.insert {
                it[id] = EntityID(3, TypeTable)
                it[name] = "PNJ"
            }
        }
    }
}

object InstanceTable : IntIdTable() {
    val currentHP = integer("current_HP").nullable()
    val currentMP = integer("current_MP").nullable()
    val x = integer("x").default(10)
    val y = integer("y").default(10)
    val idSize = reference("ID_Size", SizeTable, onDelete = ReferenceOption.CASCADE).default(EntityID(2, SizeTable))
    val visible = integer("Visible").default(0)
    val orientation = integer("Orientation").default(0)
    val idScene = integer("ID_Scene").references(SceneTable.id).default(0)
    val idBlueprint = integer("id_blueprint").references(BlueprintTable.id).default(0)
}

object SizeTable : IntIdTable() {
    val size = varchar("Size", 10)
    val value = integer("Value")

    fun initialize() {
        if (SizeTable.select((id eq 1) and (size eq "XS") and (value eq 30)).count() <= 0) {
            SizeTable.insert {
                it[id] = EntityID(1, TypeTable)
                it[size] = "XS"
                it[value] = 30
            }
        }

        if (SizeTable.select((id eq 2) and (size eq "S") and (value eq 60)).count() <= 0) {
            SizeTable.insert {
                it[id] = EntityID(2, TypeTable)
                it[size] = "S"
                it[value] = 60
            }
        }

        if (SizeTable.select((id eq 3) and (size eq "M") and (value eq 120)).count() <= 0) {
            SizeTable.insert {
                it[id] = EntityID(3, TypeTable)
                it[size] = "M"
                it[value] = 120
            }
        }

        if (SizeTable.select((id eq 4) and (size eq "L") and (value eq 200)).count() <= 0) {
            SizeTable.insert {
                it[id] = EntityID(4, TypeTable)
                it[size] = "L"
                it[value] = 200
            }
        }

        if (SizeTable.select((id eq 5) and (size eq "XL") and (value eq 300)).count() <= 0) {
            SizeTable.insert {
                it[id] = EntityID(5, TypeTable)
                it[size] = "XL"
                it[value] = 300
            }
        }

        if (SizeTable.select((id eq 6) and (size eq "XXL") and (value eq 400)).count() <= 0) {
            SizeTable.insert {
                it[id] = EntityID(6, TypeTable)
                it[size] = "XXL"
                it[value] = 400
            }
        }
    }
}

object SettingsTable : IntIdTable() {
    val name = varchar("name", 255)
    val value = varchar("value", 255).default("")

    fun initialize() {
        if (SettingsTable.select((id eq 1) and (name eq "autoUpdate")).count() <= 0) {
            SettingsTable.insert {
                it[id] = EntityID(1, SettingsTable)
                it[name] = "autoUpdate"
                it[value] = true.toString()
            }
        }
    }
}