package jdr.exia.system

import java.io.File

/**
 * Path to the Olebo directory
 */
val OLEBO_DIRECTORY = "${OS.current.appDataDir}${File.separator}Olebo${File.separator}"

/**
 * Return an extension if the String is a path to a file. If not or if it does not have an extension, it return null
 */
val String.extension
    get() = substringAfterLast('.', "").takeIf { it.isNotBlank() }