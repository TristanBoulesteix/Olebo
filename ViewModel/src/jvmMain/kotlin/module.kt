package fr.olebo.viewmodel

import fr.olebo.domain.viewmodels.StartupViewModel
import org.kodein.di.DI
import org.kodein.di.bindProvider
import org.kodein.di.new

val viewModelModule by DI.Module {
    bindProvider<StartupViewModel> { new(::StartupViewModelImpl) }
}