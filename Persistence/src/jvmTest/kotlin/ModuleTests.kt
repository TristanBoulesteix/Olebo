package fr.olebo.persistence.tests

import fr.olebo.domain.models.ConfigurationItem
import fr.olebo.persistence.LegacyTables
import fr.olebo.persistence.persistenceModule
import org.jetbrains.exposed.sql.Table
import org.kodein.di.DI
import org.kodein.di.bindSet
import org.kodein.di.direct
import org.kodein.di.instanceOrNull
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertNotNull

class ModuleTests {
    @Test
    fun `check that the correct legacy table names are actually injected`() {
        val di = DI {
            bindSet<ConfigurationItem> {

            }
            import(persistenceModule)
        }

        val table = di.direct.instanceOrNull<LegacyTables>()

        assertNotNull(table)

        assertContains(table.map(Table::tableName), "Priority")
    }
}