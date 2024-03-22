package fr.olebo.tests

import androidx.compose.runtime.*
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.window.ApplicationScope
import fr.olebo.MainContent
import fr.olebo.domain.coroutine.ApplicationIoScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.direct
import org.kodein.di.instance
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ApplicationTests {
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun `check that scope is unloaded when the application unload`() = runComposeUiTest {
        val di = DI {
            bindSingleton<ApplicationIoScope> {
                object : ApplicationIoScope, CoroutineScope by CoroutineScope(Dispatchers.Default) {}
            }
        }
        assertTrue(di.direct.instance<ApplicationIoScope>().isActive)

        val applicationScope = object : ApplicationScope {
            override fun exitApplication() = Unit
        }

        setContent {
            var isContentDisplayed by remember { mutableStateOf(true) }

            if(isContentDisplayed) {
                applicationScope.MainContent(di)
            }

            LaunchedEffect(Unit) {
                isContentDisplayed = false
            }
        }

        assertFalse(di.direct.instance<ApplicationIoScope>().isActive)
    }
}