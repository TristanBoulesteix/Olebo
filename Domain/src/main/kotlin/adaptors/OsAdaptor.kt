package fr.olebo.domain.adaptors

import fr.olebo.domain.model.system.OS

interface OsAdaptor {
    val current: OS
}