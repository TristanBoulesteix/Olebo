package fr.olebo.domain.navigation

import fr.olebo.domain.models.scenario.ScenarioInfo

sealed interface Route

object HomeScreen: Route

data class ScenarioScreen(val scenarioInfo: ScenarioInfo): Route