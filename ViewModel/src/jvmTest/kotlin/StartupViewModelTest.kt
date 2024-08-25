package fr.olebo.viewmodel.tests

import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.mock
import dev.mokkery.verify
import dev.mokkery.verify.VerifyMode.Companion.exactly
import fr.olebo.domain.adaptors.memory.RecentScenarioAdaptor
import fr.olebo.domain.viewmodels.StartupViewModel
import fr.olebo.viewmodel.StartupViewModelImpl
import kotlin.test.BeforeTest
import kotlin.test.Test

internal class StartupViewModelTest {
    private lateinit var recentScenarioAdaptor: RecentScenarioAdaptor

    private lateinit var startupViewModel: StartupViewModel

    @BeforeTest
    fun initialize() {
        recentScenarioAdaptor = mock {
            every { getRecentScenarios() } returns emptyList()
        }

        startupViewModel = StartupViewModelImpl(recentScenarioAdaptor)
    }

    @Test
    fun `get recent scenarios and validate cache behavior`() {
        startupViewModel.getRecentScenarios()
        startupViewModel.getRecentScenarios()

        verify(exactly(1)) {
            recentScenarioAdaptor.getRecentScenarios()
        }
    }
}