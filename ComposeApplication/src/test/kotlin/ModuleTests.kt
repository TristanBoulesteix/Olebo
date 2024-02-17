package fr.olebo

import org.koin.test.KoinTest
import org.koin.test.check.checkModules
import kotlin.test.Test

internal class ModuleTests: KoinTest {
    @Test
    fun `test Koin Persistence module`() {
        getKoinApplication().checkModules()
    }
}