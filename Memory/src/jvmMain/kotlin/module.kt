package fr.olebo.memory

import fr.olebo.domain.adaptors.memory.RecentScenarioAdaptor
import fr.olebo.domain.adaptors.memory.ThemeStorageAdaptor
import fr.olebo.memory.adaptors.RecentScenarioAdaptorImpl
import fr.olebo.memory.adaptors.ThemeStorageAdaptorImpl
import fr.olebo.memory.services.PreferenceService
import fr.olebo.memory.services.PreferenceServiceImpl
import org.kodein.di.DI
import org.kodein.di.bindProvider
import org.kodein.di.new
import java.util.prefs.Preferences

val memoryModule by DI.Module {
    bindProvider<Preferences> { Preferences.userRoot() }
    bindProvider<PreferenceService> { new(::PreferenceServiceImpl) }
    bindProvider<RecentScenarioAdaptor> { new(::RecentScenarioAdaptorImpl) }
    bindProvider<ThemeStorageAdaptor> { new(::ThemeStorageAdaptorImpl) }
}