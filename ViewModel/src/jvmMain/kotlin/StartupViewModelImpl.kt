package fr.olebo.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import fr.olebo.domain.adaptors.memory.RecentScenarioAdaptor
import fr.olebo.domain.models.scenario.ScenarioInfo
import fr.olebo.domain.viewmodels.StartupViewModel

internal class StartupViewModelImpl(val recentScenarioAdaptor: RecentScenarioAdaptor) : StartupViewModel {
    private var cachedRecentScenarios: List<ScenarioInfo>? by mutableStateOf(null)

    override fun getRecentScenarios(): List<ScenarioInfo> {
        if (cachedRecentScenarios == null) {
            val recentProjects = recentScenarioAdaptor.getRecentScenarios()
            cachedRecentScenarios = recentProjects
            return recentProjects
        } else {
            return cachedRecentScenarios!!
        }
    }

    override fun createScenario() {
        TODO("Not yet implemented")
    }
}