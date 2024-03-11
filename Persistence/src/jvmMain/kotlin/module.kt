package fr.olebo.persistence

import fr.olebo.persistence.tables.BaseInfo
import fr.olebo.persistence.tables.SettingsTable
import org.jetbrains.exposed.sql.Table
import org.kodein.di.DI
import org.kodein.di.bindProvider
import org.kodein.di.bindSingleton
import org.kodein.di.instance
import java.nio.file.Path
import kotlin.io.path.div

val persistenceModule by DI.Module {
    bindSingleton {
        object : DatabaseConfig {
            override val databaseFilePath = Path.of(instance<String>("olebo-directory")) / "database.db"

            override val connectionString = "jdbc:sqlite:${databaseFilePath.toAbsolutePath()}"
        }
    }
    bindProvider("legacyTablesName") { listOf("Priority") }
    bindProvider<List<Table>> { listOf(BaseInfo(di), SettingsTable(di)) }
}

internal interface DatabaseConfig {
    val databaseFilePath: Path

    val connectionString: String
}