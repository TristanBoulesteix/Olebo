package jdr.exia.model.utils

import jdr.exia.main
import java.io.File
import javax.swing.ImageIcon

/**
 * Path to the Olebo directory
 */
val OLEBO_DIRECTORY = "$appDatas${File.separator}Olebo${File.separator}"

/**
 * Get icon from name in ressources
 *
 * @param name The name of the ressource
 * @param controllerClass The class of the controller
 * @param extension (optionnal) The extension of the picture. The defaut extension is ".png"
 */
fun getIcon(name: String, controllerClass: Class<*>, extension: String = ".png"): ImageIcon =
    ImageIcon(controllerClass.classLoader.getResource("icons/$name$extension"))

/**
 * Save a picture to img folder
 *
 * @param path The path of the picture to save
 */
fun saveImg(path: String): String {
    val img = File.createTempFile(
        "img_",
        "_background.png",
        File(OLEBO_DIRECTORY + "img${File.separator}").apply { this.mkdirs() }
    )

    File(path).copyTo(img, true)

    return img.absolutePath
}

/**
 * Get the appdata path depending on the pateform.
 *
 * Available for Windows, Mac Os and Linux
 */
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

/**
 * Get the path of Olebo
 */
val jarPath: String
    get() = File(::main::class.java.protectionDomain.codeSource.location.toURI()).absolutePath

val oleboUpdater: String
    get() {
        val jar = File("${OLEBO_DIRECTORY}oleboUpdater.jar")

        if (!jar.exists()) {
            ::main.javaClass.classLoader.getResourceAsStream("updater/OleboUpdater.jar")!!.use { input ->
                jar.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
        }

        return jar.absolutePath
    }