package jdr.exia.model.tools

import java.io.File

fun File?.isFileValid() = this != null && exists() && isFile