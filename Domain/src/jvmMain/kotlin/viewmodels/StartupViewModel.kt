package fr.olebo.domain.viewmodels

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import fr.olebo.domain.adaptors.memory.RecentScenarioAdaptor
import fr.olebo.domain.models.scenario.ScenarioInfo

@Stable
open class StartupViewModel internal constructor(val recentScenarioAdaptor: RecentScenarioAdaptor) : ViewModel() {
    private var cachedRecentScenarios: List<ScenarioInfo>? by mutableStateOf(null)

    @Stable
    var scenarioInCreation by mutableStateOf(false)
        private set

    @Stable
    fun getRecentScenarios(): List<ScenarioInfo> {
        if (cachedRecentScenarios == null) {
            val recentProjects = recentScenarioAdaptor.getRecentScenarios()
            cachedRecentScenarios = recentProjects
            return recentProjects
        } else {
            return cachedRecentScenarios!!
        }
    }

    @Stable
    fun validateScenarioName(name: String) = name.isNotBlank() && name.length <= 20

    fun createScenario() {
        scenarioInCreation = true
    }

    fun cancelScenarioCreation() {
        scenarioInCreation = false
    }

    fun createAndLaunchScenario(scenarioName: String) {
        TODO("Not yet implemented")
    }
}