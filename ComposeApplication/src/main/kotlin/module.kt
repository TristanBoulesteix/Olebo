package fr.olebo

import fr.olebo.domain.adaptors.system.OsAdaptor
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.koin.fileProperties
import java.io.File
import fr.olebo.domain.module as domainModule
import fr.olebo.persistence.module as persistenceModule
import fr.olebo.system.module as systemModule

fun getKoinApplication() = startKoin {
    modules(module, domainModule, persistenceModule, systemModule)
    fileProperties()
    properties(mapOf("olebo.directory" to "${koin.get<OsAdaptor>().current.appDataDir}${File.separator}Olebo${File.separator}"))
}

private val module = module {

}

