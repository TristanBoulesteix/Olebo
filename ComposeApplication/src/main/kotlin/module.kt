package fr.olebo

import fr.olebo.domain.adaptors.system.OsAdaptor
import fr.olebo.domain.domainModule
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
}

val injector = DI {
    import(systemModule)
    import(domainModule)
    import(persistenceModule)
    import(module)
}