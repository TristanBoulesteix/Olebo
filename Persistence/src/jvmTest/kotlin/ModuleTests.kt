package fr.olebo.persistence.tests

import fr.olebo.persistence.persistenceModule
import org.kodein.di.DI
import org.kodein.di.direct
import org.kodein.di.instanceOrNull
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertNotNull

class ModuleTests {
    @Test
    fun `check that the correct legacy table names are actually injected`() {
        val di = DI {
            import(persistenceModule)
        }

        val table = di.direct.instanceOrNull<List<String>>("legacyTablesName")

        assertNotNull(table)

        assertContains(table, "Priority")
    }
}