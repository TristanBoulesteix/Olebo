package fr.olebo.update

import java.io.File

@JvmInline
value class ReleaseDirectory private constructor(val file: File) {
    companion object {
        fun getFromParent(parent: File): List<ReleaseDirectory> {
            if (!parent.isDirectory)
                return emptyList()

            return parent.listFiles()?.mapNotNull { ReleaseDirectory(it).validateOrNull() } ?: emptyList()
        }
    }

    val data
        get() = file.name.split('_')

    private fun validateOrNull() = if (data.size != 2 || data[0].toIntOrNull() == null) null else this
}

val ReleaseDirectory.versionCode
    get() = data.first().toInt()

val ReleaseDirectory.versionName
    get() = data[1]

val ReleaseDirectory.installerPaths: List<String>
    get() {
        val files: Array<File> = file.listFiles() ?: emptyArray()

        return files.map { it.absolutePath }
    }