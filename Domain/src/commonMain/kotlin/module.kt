package fr.olebo.domain

import org.kodein.di.DI

val domainModule by DI.Module {
    specializedInjection()
}

internal expect fun DI.Builder.specializedInjection()