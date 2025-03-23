@file:Suppress("PackageDirectoryMismatch")

package fr.olebo.dev

import fr.olebo.Application
import fr.olebo.application.oleboApplication
import fr.olebo.injector
import org.jetbrains.compose.reload.DevelopmentEntryPoint

fun main() = oleboApplication(injector) {
    DevelopmentEntryPoint {
        Application()
    }
}