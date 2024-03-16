package fr.olebo.persistence.tables

import fr.olebo.domain.models.Configurations
import fr.olebo.domain.models.OleboConfiguration
import fr.olebo.domain.models.get
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.upsert

internal object BaseInfo : Table(), Initializable {
    internal const val BASE_VERSION = "base_version"

    private val keyInfo = varchar("key_info", 50)

    private val value = varchar("value", 50)

    override val primaryKey = PrimaryKey(keyInfo)

    val versionBase
        get() = transaction { select(value).where(keyInfo eq BASE_VERSION).lastOrNull()?.get(value)?.toIntOrNull() }

    override fun initialize(configurations: Configurations): Unit = transaction {
        upsert {
            it[keyInfo] = BASE_VERSION
            it[value] = configurations.get<OleboConfiguration>().versionCode.toString()
        }
    }
}