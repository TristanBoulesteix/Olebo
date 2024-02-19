package fr.olebo.persistence.services

import org.jetbrains.exposed.sql.Database
import org.kodein.di.DI
import org.kodein.di.DIAware

internal class DatabaseService(override val di: DI) : DIAware {
    val database: Database = null!!
}