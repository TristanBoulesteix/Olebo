package jdr.exia.model.utils

import java.io.File
import javax.swing.ImageIcon


fun getIcon(name: String, controllerClass: Class<*>, extension: String = ".png"): ImageIcon {
    return ImageIcon(controllerClass.classLoader.getResource("icons/$name$extension"))
}


val appDatas: String
    get() {
        val os = System.getProperty("os.name").toUpperCase()

        return when {
            os.contains("WIN") -> System.getenv("APPDATA")
            os.contains("MAC") -> System.getProperty("user.home") + "/Library/"
            os.contains("NUX") -> System.getProperty("user.home")
            else -> System.getProperty("user.dir")
        } + File.separator
    }