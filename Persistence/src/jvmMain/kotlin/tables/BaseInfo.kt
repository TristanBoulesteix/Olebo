package fr.olebo.persistence.tables

import fr.olebo.domain.models.OleboConfiguration
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.upsert
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.instance

internal class BaseInfo(override val di: DI) : Table(), Initializable, DIAware {
    private val keyInfo = varchar("key_info", 50)

    private val value = varchar("value", 50)

    override val primaryKey = PrimaryKey(keyInfo)

    val versionBase
        get() = transaction { select(value).where(keyInfo eq BASE_VERSION).lastOrNull()?.get(value)?.toIntOrNull() }

    override fun initialize(): Unit = transaction {
        upsert {
            val configuration by instance<OleboConfiguration>()

            it[keyInfo] = BASE_VERSION
            it[value] = configuration.versionCode.toString()
        }
    }

    internal companion object {
        const val BASE_VERSION = "base_version"
    }
}