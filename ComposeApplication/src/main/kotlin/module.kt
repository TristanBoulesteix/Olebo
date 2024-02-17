package fr.olebo

import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.koin.fileProperties
import java.io.File
import fr.olebo.domain.module as domainModule
import fr.olebo.persistence.module as persistenceModule

fun getKoinApplication() = startKoin {
    modules(module, domainModule, persistenceModule)
    fileProperties()
    properties(mapOf("olebo.directory" to "${OS.current.appDataDir}${File.separator}Olebo${File.separator}"))
}

private val module = module {

}

enum class OS(val appDataDir: String, val executableFileTypes: Set<String> = emptySet()) {
    WINDOWS(System.getenv("APPDATA"), setOf("exe", "msi")),
    MAC_OS(System.getProperty("user.home") + "/Library/"),
    GNU_LINUX(System.getProperty("user.home")),
    OTHER(System.getProperty("user.dir"));

    companion object {
        val current
            get() = with(System.getProperty("os.name")) {
                when {
                    contains("WIN", ignoreCase = true) -> WINDOWS
                    contains("MAC", ignoreCase = true) -> MAC_OS
                    contains("NUX", ignoreCase = true) -> GNU_LINUX
                    else -> WINDOWS
                }
            }
    }
}