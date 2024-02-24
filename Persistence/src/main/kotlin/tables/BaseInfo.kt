package fr.olebo.persistence.tables

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.transactions.transaction

internal class BaseInfo : Table() {
    val keyInfo = varchar("key_info", 50)

    val value = varchar("value", 50)

    override val primaryKey = PrimaryKey(keyInfo)

    val versionBase
        get() = transaction { select(value).where(keyInfo eq BASE_VERSION).firstOrNull()?.get(value)?.toIntOrNull()  }

    companion object {
        const val BASE_VERSION = "base_version"
    }
}