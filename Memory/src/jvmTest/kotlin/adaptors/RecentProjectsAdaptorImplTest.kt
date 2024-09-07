package fr.olebo.memory.tests.adaptors

import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.mock
import dev.mokkery.verify
import fr.olebo.domain.models.scenario.ScenarioInfo
import fr.olebo.memory.adaptors.RECENT_PROJECTS_KEY
import fr.olebo.memory.adaptors.RecentScenarioAdaptorImpl
import fr.olebo.memory.services.PreferenceService
import fr.olebo.tests.utils.serializer
import kotlin.io.path.Path
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertTrue

class RecentProjectsAdaptorImplTest {
    private lateinit var emptyPreferenceService: PreferenceService

    private lateinit var emptyAdaptor: RecentScenarioAdaptorImpl

    private lateinit var preferenceService: PreferenceService

    private lateinit var adaptor: RecentScenarioAdaptorImpl

    private lateinit var expectedRecentProjects: Set<ScenarioInfo>

    @BeforeTest
    fun initialize() {
        emptyPreferenceService = mock {
            every { get(serializer<Set<ScenarioInfo>?>(), RECENT_PROJECTS_KEY) } returns null
        }

        emptyAdaptor = RecentScenarioAdaptorImpl(emptyPreferenceService)

        expectedRecentProjects = setOf(ScenarioInfo("scenario", 2, Path("")), ScenarioInfo("scenario", 3, Path("")))

        preferenceService = mock {
            every { get(serializer<Set<ScenarioInfo>?>(), RECENT_PROJECTS_KEY) } returns expectedRecentProjects
        }

        adaptor = RecentScenarioAdaptorImpl(preferenceService)
    }

    @Test
    fun `get an empty list for recent projects if no projects are stored`() {
        assertTrue(emptyAdaptor.getRecentScenarios().isEmpty())
    }

    @Test
    fun `get a list of projects if some are stored`() {
        assertContentEquals(adaptor.getRecentScenarios(), expectedRecentProjects as Iterable<ScenarioInfo>)
    }

    @Test
    fun `clear recent projects`() {
        adaptor.clearRecentProjects()

        verify {
            preferenceService[serializer<List<ScenarioInfo>>(), RECENT_PROJECTS_KEY] = emptyList()
        }
    }

    @Test
    fun `add a project to recent projects`() {
        val newRecentProject = ScenarioInfo("scenario", 1, Path(""))

        val recentProjects = expectedRecentProjects.toMutableSet()
        recentProjects += newRecentProject

        adaptor.addRecentProject(newRecentProject)

        verify {
            preferenceService[serializer<Set<ScenarioInfo>>(), RECENT_PROJECTS_KEY] = recentProjects
        }
    }
}