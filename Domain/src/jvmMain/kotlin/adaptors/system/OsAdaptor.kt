package fr.olebo.domain.adaptors.system

import fr.olebo.domain.models.system.OS

interface OsAdaptor {
    val current: OS
}