package fr.olebo.persistence.tests.tables

import fr.olebo.persistence.DatabaseConfig
import fr.olebo.persistence.services.DatabaseService
import fr.olebo.persistence.tests.jdbcConnection
import fr.olebo.persistence.tests.testConnectionString
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.transactions.transaction
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import java.sql.Connection
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

abstract class TableTests<T: Table>(private val buildTable: () -> T) {
    private lateinit var di: DI

    private lateinit var connection: Connection

    protected lateinit var table: T
        private set

    @BeforeTest
    fun test() {
        connection = jdbcConnection

        di = DI {
            bindSingleton<DatabaseConfig> {
                object : DatabaseConfig {
                    override val connectionString = testConnectionString
                }
            }
        }

        val database = DatabaseService(di).database

        table = buildTable()

        transaction(database) {
            SchemaUtils.create(table)
        }
    }

    @AfterTest
    fun clear() {
        connection.close()
    }
}