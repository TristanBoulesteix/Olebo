@file:JvmName("Olebo")

package fr.olebo

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.application
import fr.olebo.domain.coroutine.ApplicationIoScope
import kotlinx.coroutines.cancel
import org.kodein.di.DI
import org.kodein.di.direct
import org.kodein.di.instance

internal const val OLEBO_VERSION_NAME = "0.2.0"

/**
 * This code must be unique between releases and must be incremented for each one
 */
internal const val OLEBO_VERSION_CODE = 10

internal fun main() = application { MainContent(injector) }

@Composable
internal fun ApplicationScope.MainContent(di: DI) {
    DisposableEffect(Unit) {
        onDispose {
            di.direct.instance<ApplicationIoScope>().cancel()
        }
    }
}