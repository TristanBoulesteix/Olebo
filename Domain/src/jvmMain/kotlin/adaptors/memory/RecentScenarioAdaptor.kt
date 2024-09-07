package fr.olebo.domain.adaptors.memory

import fr.olebo.domain.models.scenario.ScenarioInfo

/**
 * Adaptor to manage recent projects
 */
interface RecentScenarioAdaptor {

    /**
     * Retrieves a list of recent projects.
     *
     * @return a list of [ScenarioInfo] representing recent projects.
     */
    fun getRecentScenarios(): Set<ScenarioInfo>

    /**
     * Adds a project to the list of recent projects.
     *
     * @param scenarioInfo the [ScenarioInfo] to be added to the list of recent projects
     */
    fun addRecentProject(scenarioInfo: ScenarioInfo)

    /**
     * Clears the list of recent projects.
     */
    fun clearRecentProjects()
}