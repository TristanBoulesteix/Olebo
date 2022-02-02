package fr.olebo.update

import jdr.exia.update.Release
import java.io.File

const val OLEBO_RELEASES_DIRECTORY = "Olebo_releases"

// For debug purpose
//val OLEBO_RELEASES_DIRECTORY = System.getProperty("user.dir") + File.separator + "test"

val releases
    get() = ReleaseDirectory.getFromParent(File(OLEBO_RELEASES_DIRECTORY))
        .map { Release(it.versionCode, it.versionName, it.installerPaths) }.sorted()