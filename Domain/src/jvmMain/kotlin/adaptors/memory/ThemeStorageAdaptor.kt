package fr.olebo.domain.adaptors.memory

import fr.olebo.domain.models.application.ThemeMode

interface ThemeStorageAdaptor {
    var currentTheme: ThemeMode
}