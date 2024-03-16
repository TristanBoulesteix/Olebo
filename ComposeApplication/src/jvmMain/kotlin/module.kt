package fr.olebo

import fr.olebo.domain.adaptors.OsAdaptor
import fr.olebo.domain.domainModule
import fr.olebo.domain.models.ConfigurationItem
import fr.olebo.domain.models.OleboConfiguration
import fr.olebo.persistence.persistenceModule
import fr.olebo.system.systemModule
import org.kodein.di.*
import java.io.File

private val module by DI.Module {
    bindSingleton("olebo-directory") {
        "${instance<OsAdaptor>().current.appDataDir}${File.separator}Olebo${File.separator}"
    }
    bindSet<ConfigurationItem> {
        add {
            provider {
                OleboConfiguration(OLEBO_VERSION_NAME, OLEBO_VERSION_CODE)
            }
        }
    }
}

val injector = DI {
    import(module)
    import(domainModule)
    import(systemModule)
    import(persistenceModule)
}