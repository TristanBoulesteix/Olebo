package jdr.exia.view.tools

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.transaction

@Composable
fun <T> rememberTransaction(db: Database? = null, statement: Transaction.() -> T): T = remember {
    transaction(db, statement)
}