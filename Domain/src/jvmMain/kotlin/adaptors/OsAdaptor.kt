package fr.olebo.domain.adaptors

import fr.olebo.domain.models.system.OS

interface OsAdaptor {
    val current: OS
}