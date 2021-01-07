package jdr.exia.model.dao

import jdr.exia.main
import jdr.exia.utils.Result
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream
import javax.swing.ImageIcon

const val OLEBO_MANIFEST_EXTENSION = "o_manifest"
const val OLEBO_MANIFEST_NAME = "manifest.$OLEBO_MANIFEST_EXTENSION"

/**
 * Path to the Olebo directory
 */
val OLEBO_DIRECTORY = "${appDatas}Olebo${File.separator}"

val imgPath = OLEBO_DIRECTORY + "img${File.separator}"

val updaterPath = "${OLEBO_DIRECTORY}oleboUpdater.jar"

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
        File(imgPath).apply { this.mkdirs() }
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
        val jar = File(updaterPath)

        ::main.javaClass.classLoader.getResourceAsStream("updater/OleboUpdater.jar")!!.use { input ->
            jar.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        return jar.absolutePath
    }

enum class ZipError {
    MISSING_FILES, DATABASE_HIGHER, EXCEPTION
}

fun zipOleboDirectory(fileDestination: File) {
    val oleboDirectory = File(OLEBO_DIRECTORY)
    val outputTempZip = File.createTempFile("Olebo", ".olebo")

    ZipOutputStream(BufferedOutputStream(FileOutputStream(outputTempZip))).use { zos ->
        File.createTempFile("o_manifest_", null).apply {
            this.writeText(DAO.DATABASE_VERSION.toString())
            zos.putNextEntry(ZipEntry(OLEBO_MANIFEST_NAME))
            this.inputStream().use { it.copyTo(zos) }
        }

        oleboDirectory.walkTopDown().forEach { file ->
            val zipFileName = file.absolutePath.removePrefix(oleboDirectory.absolutePath).removePrefix(File.separator)
                .replace('\\', '/')

            if (zipFileName.isNotBlank() && file.nameWithoutExtension != "oleboUpdater" && file.extension != OLEBO_MANIFEST_EXTENSION) {
                val entry = ZipEntry("$zipFileName${(if (file.isDirectory) "/" else "")}")
                zos.putNextEntry(entry)
                if (file.isFile) {
                    file.inputStream().use { it.copyTo(zos) }
                }
            }
        }
    }

    if (fileDestination.exists())
        fileDestination.deleteRecursively()

    outputTempZip.copyRecursively(fileDestination)
}

fun loadOleboZipData(zipFile: File): Result<ZipError> = try {
    ZipFile(zipFile).use { zip ->
        with(zip.entries().asSequence().toList()) {
            if (this.none { it.name == "db/${DAO.DATABASE_NAME}" } || this.none { it.name == OLEBO_MANIFEST_NAME })
                return Result.Failed(ZipError.MISSING_FILES)

            this.find { it.name == OLEBO_MANIFEST_NAME }?.let { entry ->
                zip.getInputStream(entry).use { stream ->
                    if (String(stream.readBytes()).toIntOrNull()?.let { it > DAO.DATABASE_VERSION } != false)
                        return Result.Failed(ZipError.DATABASE_HIGHER)
                }
            }

            File(OLEBO_DIRECTORY).let {
                if (it.exists())
                    it.deleteRecursively()
                it.mkdirs()
            }

            this.filter { it.name != OLEBO_MANIFEST_NAME }.forEach { entry ->
                val fileString = entry.name.removeSuffix('/'.toString()).split('/').let { splitedName ->
                    splitedName.dropLast(1).joinToString('/'.toString()).replace('/', File.separatorChar).let {
                        OLEBO_DIRECTORY + it + (if (it.isNotBlank()) File.separator else "") + splitedName.last()
                    }
                }
                if (entry.isDirectory) {
                    File(fileString).mkdirs()
                } else {
                    zip.getInputStream(entry).use { input ->
                        File(fileString).outputStream().use {
                            input.copyTo(it)
                        }
                    }
                }
            }
        }
    }

    Result.Success()
} catch (e: Exception) {
    e.printStackTrace()
    Result.Failed(ZipError.EXCEPTION)
}