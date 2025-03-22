package fr.olebo.domain.adaptors.memory

import fr.olebo.domain.models.system.Language

interface SettingsStorageAdaptor {
    var applicationLanguage: Language
}