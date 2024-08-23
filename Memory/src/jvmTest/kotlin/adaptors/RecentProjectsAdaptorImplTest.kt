package fr.olebo.memory.tests.adaptors

import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.mock
import dev.mokkery.verify
import fr.olebo.domain.models.scenario.ScenarioInfo
import fr.olebo.memory.adaptors.RECENT_PROJECTS_KEY
import fr.olebo.memory.adaptors.RecentProjectsAdaptorImpl
import fr.olebo.memory.services.PreferenceService
import fr.olebo.tests.utils.serializer
import kotlin.io.path.Path
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertTrue

class RecentProjectsAdaptorImplTest {
    private lateinit var emptyPreferenceService: PreferenceService

    private lateinit var emptyAdaptor: RecentProjectsAdaptorImpl

    private lateinit var preferenceService: PreferenceService

    private lateinit var adaptor: RecentProjectsAdaptorImpl

    private lateinit var expectedRecentProjects: List<ScenarioInfo>

    @BeforeTest
    fun initialize() {
        emptyPreferenceService = mock {
            every { get(serializer<List<ScenarioInfo>?>(), RECENT_PROJECTS_KEY) } returns null
        }

        emptyAdaptor = RecentProjectsAdaptorImpl(emptyPreferenceService)

        expectedRecentProjects = listOf(ScenarioInfo(Path(""), "scenario", 2), ScenarioInfo(Path(""), "scenario", 3))

        preferenceService = mock {
            every { get(serializer<List<ScenarioInfo>?>(), RECENT_PROJECTS_KEY) } returns expectedRecentProjects
        }

        adaptor = RecentProjectsAdaptorImpl(preferenceService)
    }

    @Test
    fun `get an empty list for recent projects if no projects are stored`() {
        assertTrue(emptyAdaptor.getRecentProjects().isEmpty())
    }

    @Test
    fun `get a list of projects if some are stored`() {
        assertContentEquals(adaptor.getRecentProjects(), expectedRecentProjects)
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
        val newRecentProject = ScenarioInfo(Path(""), "scenario", 1)

        val recentProjects = expectedRecentProjects.toMutableList()
        recentProjects += newRecentProject

        adaptor.addRecentProject(newRecentProject)

        verify {
            preferenceService[serializer<List<ScenarioInfo>>(), RECENT_PROJECTS_KEY] = recentProjects
        }
    }
}