@file:JvmName("Olebo")

package fr.olebo

import fr.olebo.application.oleboApplication

internal const val OLEBO_VERSION_NAME = "0.2.0"

/**
 * This code must be unique between releases and must be incremented for each one
 */
internal const val OLEBO_VERSION_CODE = 10

internal fun main() = oleboApplication { }