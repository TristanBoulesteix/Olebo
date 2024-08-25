package fr.olebo.memory.adaptors

import fr.olebo.domain.adaptors.memory.RecentScenarioAdaptor
import fr.olebo.domain.models.scenario.ScenarioInfo
import fr.olebo.memory.services.PreferenceService
import fr.olebo.memory.services.get
import fr.olebo.memory.services.set

internal const val RECENT_PROJECTS_KEY = "recentProjects"

internal class RecentScenarioAdaptorImpl(private val preferenceService: PreferenceService) : RecentScenarioAdaptor {
    override fun getRecentScenarios(): List<ScenarioInfo> = preferenceService[RECENT_PROJECTS_KEY] ?: emptyList()

    override fun addRecentProject(scenarioInfo: ScenarioInfo) {
        val recentProjects = getRecentScenarios().toMutableList()
        recentProjects += scenarioInfo
        preferenceService[RECENT_PROJECTS_KEY] = recentProjects
    }

    override fun clearRecentProjects() {
        preferenceService[RECENT_PROJECTS_KEY] = emptyList<ScenarioInfo>()
    }
}