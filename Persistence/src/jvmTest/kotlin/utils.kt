package fr.olebo.persistence.tests

import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.mock
import fr.olebo.persistence.tests.model.FileSystemProvider
import java.nio.file.FileSystem
import java.sql.Connection
import java.sql.DatabaseMetaData
import java.sql.DriverManager
import java.nio.file.Path as JavaPath

internal const val testConnectionString = "jdbc:sqlite:file:test?mode=memory&cache=shared"

internal val jdbcConnection: Connection
    get() = DriverManager.getConnection(testConnectionString)

internal fun checkColumnsOf(tableName: String): Set<ColumnData> = jdbcConnection.use {
    val resultSet = it.metaData.getColumns(null, null, tableName, null)
    val primaryKeys = it.metaData.getPrimaryKeys(null, null, tableName)

    val primaryKeysNames = buildSet {
        while (primaryKeys.next()) {
            add(primaryKeys.getString("COLUMN_NAME"))
        }
    }

    buildSet {
        while (resultSet.next()) {
            val name = resultSet.getString("COLUMN_NAME")
            val type = resultSet.getString("TYPE_NAME")
            var defaultValue: String? = resultSet.getString("COLUMN_DEF")

            if (type == "VARCHAR") {
                defaultValue = defaultValue?.trim('\'')
            }

            add(
                ColumnData(
                    name,
                    type,
                    resultSet.getInt("NULLABLE") == DatabaseMetaData.columnNullable,
                    name in primaryKeysNames,
                    defaultValue,
                    resultSet.getInt("COLUMN_SIZE")
                )
            )
        }
    }
}

fun buildMockedPath(): JavaPath {
    val fileSystemImpl = mock<FileSystem> {
        every { provider() } returns FileSystemProvider()
    }

    val parentPath = mock<Path> {
        every { fileSystem } returns fileSystemImpl
    }

    return mock<Path> {
        every { fileSystem } returns fileSystemImpl
        every { parent } returns parentPath
    }
}

internal data class ColumnData(
    val name: String,
    val type: String,
    val isNullable: Boolean = false,
    val isPrimary: Boolean = false,
    val defaultValue: String? = null,
    val length: Int = 0
) {
    companion object {
        val intId
            get() = ColumnData(
                "id",
                "INTEGER",
                isPrimary = true,
                length = 2000000000,
                isNullable = true,
                defaultValue = null
            )
    }
}

private interface Path : JavaPath