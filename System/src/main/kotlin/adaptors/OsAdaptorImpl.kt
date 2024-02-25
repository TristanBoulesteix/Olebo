package fr.olebo.system.adaptors

import adaptors.OsAdaptor
import models.OS

internal class OsAdaptorImpl : OsAdaptor {
    override val current
        get() = with(System.getProperty("os.name")) {
            when {
                contains("WIN", ignoreCase = true) -> OS.WINDOWS
                contains("MAC", ignoreCase = true) -> OS.MAC_OS
                contains("NUX", ignoreCase = true) -> OS.GNU_LINUX
                else -> OS.WINDOWS
            }
        }
}