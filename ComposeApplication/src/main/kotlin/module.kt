package fr.olebo

import fr.olebo.domain.domainModule
import fr.olebo.persistence.persistenceModule
import fr.olebo.system.systemModule
import org.kodein.di.DI

private val module by DI.Module {

}

val injector = DI {
    import(module)
    import(domainModule)
    import(systemModule)
    import(persistenceModule)
}