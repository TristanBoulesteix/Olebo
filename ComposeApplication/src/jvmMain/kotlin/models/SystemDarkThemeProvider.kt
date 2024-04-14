package fr.olebo.models

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable

fun interface SystemDarkThemeProvider {
    @Composable
    @ReadOnlyComposable
    operator fun invoke(): Boolean
}