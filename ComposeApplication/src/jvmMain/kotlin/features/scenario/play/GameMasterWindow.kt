package fr.olebo.features.scenario.play

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.WindowPlacement
import fr.olebo.application.style.fullScreenWindowDimension
import fr.olebo.components.OleboWindow
import fr.olebo.domain.models.scenario.ScenarioInfo
import fr.olebo.resources.Res
import fr.olebo.resources.game_master_window_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun ApplicationScope.GameMasterWindow(scenarioInfo: ScenarioInfo) {
    OleboWindow(
        title = stringResource(Res.string.game_master_window_title, scenarioInfo.name),
        size = fullScreenWindowDimension,
        minimumSize = fullScreenWindowDimension,
        placement = WindowPlacement.Maximized,
    ) {

    }
}