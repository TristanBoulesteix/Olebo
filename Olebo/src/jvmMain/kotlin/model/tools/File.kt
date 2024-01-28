package jdr.exia.model.tools

import java.io.File
import java.nio.file.Path
import java.nio.file.Paths

fun File?.isFileValid() = this != null && exists() && isFile

fun String.toPath(): Path = Paths.get(this)