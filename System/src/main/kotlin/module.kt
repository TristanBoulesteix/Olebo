package fr.olebo.system

import fr.olebo.domain.adaptors.system.OsAdaptor
import fr.olebo.system.adaptors.OsAdaptorImpl
import org.kodein.di.DI
import org.kodein.di.bindProviderOf

val systemModule by DI.Module {
    bindProviderOf<OsAdaptor>(::OsAdaptorImpl)
}