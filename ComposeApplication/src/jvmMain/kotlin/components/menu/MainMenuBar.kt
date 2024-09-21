package fr.olebo.components.menu

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.MenuBar
import androidx.compose.ui.window.MenuBarScope
import androidx.compose.ui.window.MenuScope
import fr.olebo.application.style.LocalThemeManager
import fr.olebo.application.style.label
import fr.olebo.domain.models.application.ThemeMode
import fr.olebo.resources.Res
import fr.olebo.resources.file_menu
import fr.olebo.resources.settings_menu_item
import fr.olebo.resources.theme_menu_item
import org.jetbrains.compose.resources.stringResource

@Composable
fun FrameWindowScope.MainMenuBar() = MenuBar {
    FileMenu()
}

@Composable
fun MenuBarScope.FileMenu() = Menu(text = stringResource(Res.string.file_menu), mnemonic = 'F') {
    Item(text = stringResource(Res.string.settings_menu_item), mnemonic = 'S', enabled = false) {

    }

    ThemeMenuItem()
}

@Composable
fun MenuScope.ThemeMenuItem() {
    val themeManager = LocalThemeManager.current

    Menu(text = stringResource(Res.string.theme_menu_item, stringResource(themeManager.mode.label)), mnemonic = 'T') {
        ThemeMode.entries.forEach {
            RadioButtonItem(
                text = stringResource(it.label),
                mnemonic = it.name.first(),
                selected = themeManager.mode == it
            ) { themeManager.mode = it }
        }
    }
}