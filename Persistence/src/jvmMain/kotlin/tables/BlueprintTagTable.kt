package fr.olebo.persistence.tables

import org.jetbrains.exposed.sql.Table

internal object BlueprintTagTable: Table() {
    val blueprint = reference("blueprint", BlueprintTable)
    val tag = reference("tag", TagTable)

    override val primaryKey = PrimaryKey(blueprint, tag)
}