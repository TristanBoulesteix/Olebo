@file:JvmName("Olebo")

package fr.olebo

import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.window.application
import fr.olebo.domain.coroutine.ApplicationIoScope
import kotlinx.coroutines.cancel
import org.kodein.di.direct
import org.kodein.di.instance

internal const val OLEBO_VERSION_NAME = "0.2.0"

/**
 * This code must be unique between releases and must be incremented for each one
 */
internal const val OLEBO_VERSION_CODE = 10

fun main() = application {
    DisposableEffect(Unit) {
        val di = injector

        onDispose {
            di.direct.instance<ApplicationIoScope>().cancel()
        }
    }
}