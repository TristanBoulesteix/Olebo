package fr.olebo.tests.application

import androidx.compose.runtime.LaunchedEffect
import fr.olebo.application.oleboApplication
import fr.olebo.domain.coroutine.ApplicationIoScope
import fr.olebo.models.SystemDarkThemeProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import org.kodein.di.*
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ApplicationTests {
    @Test
    fun `check that scope is unloaded when the application unload`() {
        val di = DI {
            bindSingleton<ApplicationIoScope> {
                object : ApplicationIoScope, CoroutineScope by CoroutineScope(Dispatchers.Default) {}
            }
            bindProvider<SystemDarkThemeProvider> { SystemDarkThemeProvider { true } }
        }
        assertTrue(di.direct.instance<ApplicationIoScope>().isActive)

        oleboApplication(di) {
            LaunchedEffect(Unit) {
                exitApplication()
            }
        }

        assertFalse(di.direct.instance<ApplicationIoScope>().isActive)
    }
}