package fr.olebo.domain.tests.viewmodels

import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.mock
import dev.mokkery.verify
import fr.olebo.domain.adaptors.memory.RecentScenarioAdaptor
import fr.olebo.domain.models.scenario.ScenarioInfo
import fr.olebo.domain.navigation.NavigationHandler
import fr.olebo.domain.navigation.ScenarioScreen
import fr.olebo.domain.viewmodels.StartupViewModel
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class StartupViewModelTest {
    // Initialize dependencies and subject under test.
    private lateinit var recentScenarioAdaptor: RecentScenarioAdaptor
    private lateinit var navigationHandler: NavigationHandler
    private lateinit var viewModel: StartupViewModel

    private lateinit var expectedRecentScenarios: Set<ScenarioInfo>

    // Set up each test with a new instance of subject under test and its dependencies.
    @BeforeTest
    fun setup() {
        expectedRecentScenarios = setOf(ScenarioInfo("scenario1"), ScenarioInfo("scenario2"))

        recentScenarioAdaptor = mock(MockMode.autoUnit) {
            every { getRecentScenarios() } returns expectedRecentScenarios
        }
        navigationHandler = mock(MockMode.autoUnit)

        viewModel = StartupViewModel(recentScenarioAdaptor, navigationHandler)
    }

    // It should be able to get recent scenarios.
    @Test
    fun `should get recent scenarios correctly`() {
        assertEquals(expectedRecentScenarios, viewModel.getRecentScenarios())
    }

    // It should validate scenario name correctly.
    @Test
    fun `should validate scenario name correctly`() {
        assertTrue(viewModel.validateScenarioName("Valid Scenario"))
        assertFalse(viewModel.validateScenarioName("Scenario with name that exceeds twenty characters"))
        assertFalse(viewModel.validateScenarioName(""))
    }

    // It should create scenario correctly.
    @Test
    fun `should create scenario correctly`() {
        assertFalse(viewModel.scenarioInCreation)
        viewModel.createScenario()
        assertTrue(viewModel.scenarioInCreation)
    }

    // It should cancel scenario creation correctly.
    @Test
    fun `should cancel scenario creation correctly`() {
        assertFalse(viewModel.scenarioInCreation)
        viewModel.createScenario()
        assertTrue(viewModel.scenarioInCreation)
        viewModel.cancelScenarioCreation()
        assertFalse(viewModel.scenarioInCreation)
    }

    // It should create and launch a scenario successfully.
    @Test
    fun `should create and launch scenario correctly`() {
        val scenarioName = "Test scenario"
        viewModel.createAndLaunchScenario(scenarioName)

        // Ensures scenario creation is no longer in progress.
        assertFalse(viewModel.scenarioInCreation)

        // Verifies the interaction with the recentScenarioAdaptor
        verify {
            recentScenarioAdaptor.addRecentProject(ScenarioInfo(scenarioName))
        }
    }

    // It should launch scenario screen correctly.
    @Test
    fun `should launch scenario screen correctly`() {
        val scenario = ScenarioInfo(name = "Test scenario")

        viewModel.launchScenario(scenario)

        // Verifies navigation to the corresponding scenario screen.
        verify {
            navigationHandler.navigate(ScenarioScreen(ScenarioInfo(scenario.name)))
        }
    }

    @Test
    fun `should cache recent scenarios correctly`() {
        assertNull(viewModel.cachedRecentScenarios)

        viewModel.getRecentScenarios()

        assertEquals(expectedRecentScenarios, viewModel.cachedRecentScenarios)
    }
}