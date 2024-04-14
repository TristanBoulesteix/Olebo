package fr.olebo.tests.application

import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import fr.olebo.application.WithDI
import fr.olebo.domain.coroutine.ApplicationIoScope
import fr.olebo.tests.applicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.isActive
import kotlinx.coroutines.test.StandardTestDispatcher
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ApplicationTests {
    private lateinit var di: DI

    @BeforeTest
    fun initialize() {
        di = DI {
            bindSingleton<ApplicationIoScope> {
                object : ApplicationIoScope, CoroutineScope by CoroutineScope(StandardTestDispatcher()) {}
            }
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun `check that scope is unloaded when the application unload`() = runComposeUiTest {
        val applicationIoScope by di.instance<ApplicationIoScope>()

        assertTrue(applicationIoScope.isActive)

        var isUiVisible by mutableStateOf(true)

        setContent {
            if (isUiVisible)
                applicationScope.WithDI(di) {
                    Spacer(Modifier.testTag("test"))
                }
        }

        isUiVisible = false
        waitForIdle()

        assertFalse(applicationIoScope.isActive)
    }
}