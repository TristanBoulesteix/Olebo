package fr.olebo.persistence

import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance
import java.io.File

val persistenceModule by DI.Module {
    bindSingleton {
        object : DatabaseConfig {
            override val filePath = "${instance<String>("olebo-directory")}db${File.separator}database.db"

            override val connectionString = "jdbc:sqlite:${filePath}"
        }
    }
}

internal interface DatabaseConfig {
    val filePath: String?
        get() = null

    val connectionString: String
}