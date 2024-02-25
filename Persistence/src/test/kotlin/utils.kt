package fr.olebo.persistence.tests

import java.sql.Connection
import java.sql.DatabaseMetaData
import java.sql.DriverManager

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

internal data class ColumnData(
    val name: String,
    val type: String,
    val isNullable: Boolean = false,
    val isPrimary: Boolean = false,
    val defaultValue: Any? = null,
    val length: Int = 0
)