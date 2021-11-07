package jdr.exia.model.dao

import jdr.exia.OLEBO_VERSION_CODE
import jdr.exia.localization.ST_WARNING_MISSING_CONF_FILES
import jdr.exia.localization.ST_WARNING_PREVIOUS_VERSION_FILE
import jdr.exia.localization.StringLocale
import jdr.exia.main
import jdr.exia.system.OLEBO_DIRECTORY
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Path
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream
import kotlin.io.path.Path
import kotlin.io.path.div
import kotlin.system.exitProcess

const val OLEBO_MANIFEST_EXTENSION = "o_manifest"
const val OLEBO_MANIFEST_NAME = "manifest.$OLEBO_MANIFEST_EXTENSION"

/**
 * Get the path of Olebo
 */
val jarPath: String
    get() = File(::main::class.java.protectionDomain.codeSource.location.toURI()).absolutePath

private val File.property
    get() = Properties().apply {
        inputStream().use(this::load)
    }

suspend fun zipOleboDirectory(fileDestination: File) = withContext(Dispatchers.IO) {
    runCatching {
        val oleboDirectory = File(OLEBO_DIRECTORY)
        val outputTempZip = File.createTempFile("Olebo", ".olebo")

        ZipOutputStream(BufferedOutputStream(FileOutputStream(outputTempZip))).use { zos ->
            File.createTempFile("o_manifest_", null).apply file@{
                property.apply {
                    setProperty("version", OLEBO_VERSION_CODE.toString())
                    this@file.outputStream().use { store(it, "") }
                }
                zos.putNextEntry(ZipEntry(OLEBO_MANIFEST_NAME))
                this.inputStream().use { it.copyTo(zos) }
            }

            oleboDirectory.walkTopDown().forEach { file ->
                val zipFileName =
                    file.absolutePath.removePrefix(oleboDirectory.absolutePath).removePrefix(File.separator)
                        .replace('\\', '/')

                if (zipFileName.isNotBlank() && file.nameWithoutExtension != "olebo_updater" && file.extension != OLEBO_MANIFEST_EXTENSION) {
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
}

suspend fun loadOleboZipData(zipFile: File) = withContext(Dispatchers.IO) {
    runCatching {
        ZipFile(zipFile).use { zip ->
            with(zip.entries().asSequence().toList()) {
                if (this.none { it.name == "db/${DAO.DATABASE_NAME}" } || this.none { it.name == OLEBO_MANIFEST_NAME })
                    return@runCatching StringLocale[ST_WARNING_MISSING_CONF_FILES]

                this.find { it.name == OLEBO_MANIFEST_NAME }?.let { entry ->
                    zip.getInputStream(entry).use { stream ->
                        val manifestVersion =
                            Properties().apply { load(stream) }.getProperty("version", 0.toString()).toIntOrNull() ?: 0

                        if (manifestVersion > OLEBO_VERSION_CODE)
                            return@runCatching StringLocale[ST_WARNING_PREVIOUS_VERSION_FILE]
                    }
                }

                reset()

                this.filter { it.name != OLEBO_MANIFEST_NAME }.forEach { entry ->
                    val fileString = entry.name.removeSuffix('/'.toString()).split('/').let { splitName ->
                        splitName.dropLast(1).joinToString('/'.toString()).replace('/', File.separatorChar).let {
                            OLEBO_DIRECTORY + it + (if (it.isNotBlank()) File.separator else "") + splitName.last()
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

        null
    }
}

fun reset() = File(OLEBO_DIRECTORY).let {
    if (it.exists())
        it.deleteRecursively()
    it.mkdirs()
}

fun restart(status: Int = 0): Nothing {
    Runtime.getRuntime().exec(Path(jarPath).parent.parent / "Olebo.exe")
    exitProcess(status)
}

private fun Runtime.exec(path: Path): Process = this.exec(path.toString())