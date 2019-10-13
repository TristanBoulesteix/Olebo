package jdr.exia.model.utils

import java.io.File
import javax.swing.ImageIcon

val OLEBO_DIRECTORY = "$appDatas${File.separator}Olebo${File.separator}"

fun getIcon(name: String, controllerClass: Class<*>, extension: String = ".png"): ImageIcon {
    return ImageIcon(controllerClass.classLoader.getResource("icons/$name$extension"))
}

fun saveImg(path: String): String {
    val img = File.createTempFile(
        "img_",
        "_background.png",
        File(OLEBO_DIRECTORY + "img${File.separator}").apply { this.mkdirs() }
    )

    File(path).copyTo(img, true)

    return img.absolutePath
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