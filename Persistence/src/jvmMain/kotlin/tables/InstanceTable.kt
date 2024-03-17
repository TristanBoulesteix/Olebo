package fr.olebo.persistence.tables

import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

internal object InstanceTable : IntIdTable() {
    val currentHP = integer("current_HP").nullable()
    val currentMP = integer("current_MP").nullable()
    val x = float("x").default(10f)
    val y = float("y").default(10f)
    val idSize = reference("ID_Size", SizeTable, onDelete = ReferenceOption.CASCADE).default(EntityID(2, SizeTable))
    val visible = bool("Visible").default(false)
    val orientation = float("Orientation").default(0f)
    val layer =
        reference("id_priority", LayerTable, onDelete = ReferenceOption.CASCADE).default(EntityID(2, LayerTable))
    val idScene = integer("ID_Scene").references(SceneTable.id).default(0)
    val idBlueprint = integer("id_blueprint").references(BlueprintTable.id).default(0)
    val deleted = bool("deleted").default(false)
    val alias = varchar("alias", 255).default("")
}