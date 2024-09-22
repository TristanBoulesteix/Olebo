package fr.olebo.memory.adaptors

import fr.olebo.domain.adaptors.memory.ThemeStorageAdaptor
import fr.olebo.domain.models.application.ThemeMode
import fr.olebo.memory.services.PreferenceService
import fr.olebo.memory.services.get
import fr.olebo.memory.services.set

internal const val THEME_KEY = "theme_storage"

internal class ThemeStorageAdaptorImpl(private val preferenceService: PreferenceService): ThemeStorageAdaptor {
    override var currentTheme: ThemeMode
        get() = preferenceService[THEME_KEY] ?: ThemeMode.Auto
        set(value) { preferenceService[THEME_KEY] = value }
}