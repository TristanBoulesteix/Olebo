package fr.olebo

import org.koin.core.context.startKoin
import org.koin.fileProperties
import java.io.File
import java.lang.System.getProperty
import java.lang.System.getenv
import fr.olebo.domain.module as domainModule
import fr.olebo.persistence.module as persistenceModule

fun main() {
    startKoin {
        modules(module, domainModule, persistenceModule)
        fileProperties()
        properties(mapOf("olebo.directory" to "${OS.current.appDataDir}${File.separator}Olebo${File.separator}"))
    }
}

enum class OS(val appDataDir: String, val executableFileTypes: Set<String> = emptySet()) {
    WINDOWS(getenv("APPDATA"), setOf("exe", "msi")),
    MAC_OS(getProperty("user.home") + "/Library/"),
    GNU_LINUX(getProperty("user.home")),
    OTHER(getProperty("user.dir"));

    companion object {
        val current
            get() = with(getProperty("os.name")) {
                when {
                    contains("WIN", ignoreCase = true) -> WINDOWS
                    contains("MAC", ignoreCase = true) -> MAC_OS
                    contains("NUX", ignoreCase = true) -> GNU_LINUX
                    else -> WINDOWS
                }
            }
    }
}