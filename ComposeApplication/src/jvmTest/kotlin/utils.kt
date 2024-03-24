package fr.olebo.tests

import androidx.compose.material.Colors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.window.ApplicationScope
import org.kodein.di.DI
import org.kodein.di.compose.withDI
import kotlin.test.assertEquals

@Stable
val applicationScope
    get() = object : ApplicationScope {
        override fun exitApplication() = Unit
    }

fun assertColorsEquals(expectedColors: Colors, actualColors: Colors, message: (String) -> String) {
    assertEquals(expectedColors.primary, actualColors.primary, message("primary"))
    assertEquals(expectedColors.primaryVariant, actualColors.primaryVariant, message("primaryVariant"))
    assertEquals(expectedColors.secondary, actualColors.secondary, message("secondary"))
    assertEquals(expectedColors.secondaryVariant, actualColors.secondaryVariant, message("secondaryVariant"))
    assertEquals(expectedColors.background, actualColors.background, message("background"))
    assertEquals(expectedColors.surface, actualColors.surface, message("surface"))
    assertEquals(expectedColors.error, actualColors.error, message("error"))
    assertEquals(expectedColors.onPrimary, actualColors.onPrimary, message("onPrimary"))
    assertEquals(expectedColors.onSecondary, actualColors.onSecondary, message("onSecondary"))
    assertEquals(expectedColors.onBackground, actualColors.onBackground, message("onBackground"))
    assertEquals(expectedColors.onSurface, actualColors.onSurface, message("onSurface"))
    assertEquals(expectedColors.onError, actualColors.onError, message("onError"))
    assertEquals(expectedColors.isLight, actualColors.isLight, message("isLight"))
}

@ExperimentalTestApi
fun ComposeUiTest.setContentWithDI(di: DI, content: @Composable () -> Unit) = setContent {
    withDI(di) { content() }
}

