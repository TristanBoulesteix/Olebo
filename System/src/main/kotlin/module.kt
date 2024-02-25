package fr.olebo.system

import adaptors.OsAdaptor
import fr.olebo.system.adaptors.OsAdaptorImpl
import org.kodein.di.DI
import org.kodein.di.bindProviderOf

val systemModule by DI.Module {
    bindProviderOf<OsAdaptor>(::OsAdaptorImpl)
}