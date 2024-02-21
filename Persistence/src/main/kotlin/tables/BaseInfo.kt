package fr.olebo.persistence.tables

import org.jetbrains.exposed.sql.Table

internal class BaseInfo : Table() {
    val keyInfo = varchar("key_info", 50)

    val value = varchar("value", 50)

    override val primaryKey = PrimaryKey(keyInfo)
}