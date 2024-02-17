package fr.olebo.system

import fr.olebo.domain.adaptors.system.OsAdaptor
import fr.olebo.system.adaptors.OsAdaptorImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val module = module {
    singleOf<OsAdaptor>(::OsAdaptorImpl)
}