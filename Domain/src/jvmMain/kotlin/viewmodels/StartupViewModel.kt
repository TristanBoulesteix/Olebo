package fr.olebo.domain.viewmodels

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import fr.olebo.domain.models.scenario.ScenarioInfo

/**
 * [Immutable] interface representing a view model for startup functionality.
 */
@Immutable
interface StartupViewModel {
    @Stable
    val scenarioInCreation: Boolean

    /**
     * Retrieves a list of recent scenarios.
     *
     * @return a list of [ScenarioInfo] representing recent scenarios.
     */
    @Stable
    fun getRecentScenarios(): List<ScenarioInfo>

    @Stable
    fun validateScenarioName(name: String): Boolean

    fun createScenario()

    fun cancelScenarioCreation()

    fun createAndLaunchScenario(scenarioName: String)
}