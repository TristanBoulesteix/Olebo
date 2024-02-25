package fr.olebo

import fr.olebo.domain.adaptors.OsAdaptor
import fr.olebo.domain.domainModule
import fr.olebo.domain.model.system.OleboConfiguration
import fr.olebo.persistence.persistenceModule
import fr.olebo.system.systemModule
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance
import java.io.File

private val module by DI.Module {
    bindSingleton("olebo-directory") {
        "${instance<OsAdaptor>().current.appDataDir}${File.separator}Olebo${File.separator}"
    }
    bindSingleton { OleboConfiguration(OLEBO_VERSION_NAME, OLEBO_VERSION_CODE) }
}

val injector = DI {
    import(systemModule)
    import(domainModule)
    import(persistenceModule)
    import(module)
}