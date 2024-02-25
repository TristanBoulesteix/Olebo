package fr.olebo.persistence.tables

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.transactions.transaction
import org.kodein.di.DI
import org.kodein.di.DIAware

internal class BaseInfo(override val di: DI) : Table(), Initializable, DIAware {
    private val keyInfo = varchar("key_info", 50)

    private val value = varchar("value", 50)

    override val primaryKey = PrimaryKey(keyInfo)

    val versionBase
        get() = transaction { select(value).where(keyInfo eq BASE_VERSION).firstOrNull()?.get(value)?.toIntOrNull() }

    override fun initialize() {
        TODO("Not yet implemented")
    }

    companion object {
        const val BASE_VERSION = "base_version"
    }
}