package fr.olebo.memory.adaptors

import fr.olebo.domain.adaptors.memory.RecentScenarioAdaptor
import fr.olebo.domain.models.scenario.ScenarioInfo
import fr.olebo.memory.services.PreferenceService
import fr.olebo.memory.services.get
import fr.olebo.memory.services.set

internal const val RECENT_PROJECTS_KEY = "recent_projects"

internal class RecentScenarioAdaptorImpl(private val preferenceService: PreferenceService) : RecentScenarioAdaptor {
    override fun getRecentScenarios(): Set<ScenarioInfo> = preferenceService[RECENT_PROJECTS_KEY] ?: emptySet()

    override fun addRecentProject(scenarioInfo: ScenarioInfo) {
        val recentProjects = getRecentScenarios().toMutableSet()
        recentProjects += scenarioInfo
        preferenceService[RECENT_PROJECTS_KEY] = recentProjects
    }

    override fun clearRecentProjects() {
        preferenceService[RECENT_PROJECTS_KEY] = emptySet<ScenarioInfo>()
    }
}