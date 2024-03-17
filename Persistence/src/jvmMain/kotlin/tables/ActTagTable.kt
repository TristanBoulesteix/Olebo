package fr.olebo.persistence.tables

import org.jetbrains.exposed.sql.Table

object ActTagTable : Table() {
    val act = reference("act", ActTable)
    val tag = reference("tag", TagTable)

    override val primaryKey = PrimaryKey(act, tag)
}