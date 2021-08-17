package fr.olebo.update

import jdr.exia.update.Release
import java.io.File

//val OLEBO_RELEASES_DIRECTORY = System.getProperty("user.home") + File.separator + "Olebo_releases"

// For debug purpose
val OLEBO_RELEASES_DIRECTORY = System.getProperty("user.dir") + File.separator + "test"

val releases
    get() = ReleaseDirectory.getFromParent(File(OLEBO_RELEASES_DIRECTORY))
        .map { Release(it.versionCode, it.versionName, it.installerPaths) }.sorted()