package fr.olebo.domain.viewmodels

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import fr.olebo.domain.models.scenario.ScenarioInfo

/**
 * [Immutable] interface representing a view model for startup functionality.
 */
@Immutable
interface StartupViewModel {
    /**
     * Retrieves a list of recent scenarios.
     *
     * @return a list of [ScenarioInfo] representing recent scenarios.
     */
    @Stable
    fun getRecentScenarios(): List<ScenarioInfo>

    fun createScenario()
}