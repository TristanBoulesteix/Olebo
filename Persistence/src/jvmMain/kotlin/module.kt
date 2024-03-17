package fr.olebo.persistence

import fr.olebo.domain.models.ConfigurationItem
import fr.olebo.domain.models.appendConfiguration
import fr.olebo.persistence.services.DatabaseService
import fr.olebo.persistence.tables.*
import org.jetbrains.exposed.sql.Table
import org.kodein.di.*
import java.nio.file.Path
import kotlin.io.path.div

val persistenceModule by DI.Module {
    bind<DI>() with contexted<DatabaseService>().provider { di }
    bindSingletonOf(::DatabaseService)
    appendConfiguration {
        object : DatabaseConfiguration {
            override val databaseFilePath = Path.of(instance<String>("olebo-directory")) / "database.db"

            override val connectionString = "jdbc:sqlite:${databaseFilePath.toAbsolutePath()}"
        }
    }
    bindProviderOf<LegacyTables>(::LegacyTablesImpl)
    bindProvider<List<Table>> {
        listOf(
            BaseInfo,
            SettingsTable,
            ActTable,
            SceneTable,
            TypeTable,
            BlueprintTable,
            LayerTable,
            SizeTable,
            InstanceTable,
            TagTable,
            BlueprintTagTable,
            ActTagTable
        )
    }
}

internal interface DatabaseConfiguration : ConfigurationItem {
    val databaseFilePath: Path

    val connectionString: String
}

internal interface LegacyTables : List<Table>

private class LegacyTablesImpl : LegacyTables, List<Table> by listOf(object : Table("Priority") {})
