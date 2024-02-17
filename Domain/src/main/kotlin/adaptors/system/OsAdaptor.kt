package fr.olebo.domain.adaptors.system

import fr.olebo.domain.model.system.OS

interface OsAdaptor {
    val current: OS
}