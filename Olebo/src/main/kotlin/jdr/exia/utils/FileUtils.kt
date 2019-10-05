package jdr.exia.utils

import java.io.File

val appDatas : String
    get() {
        val os = System.getProperty("os.name").toUpperCase()

        return when {
            os.contains("WIN") -> System.getenv("APPDATA")
            os.contains("MAC") -> System.getProperty("user.home") + "/Library/"
            os.contains("NUX") -> System.getProperty("user.home")
            else -> System.getProperty("user.dir")
        } + File.separator
    }