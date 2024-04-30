package fr.olebo.domain.persistence

import org.jetbrains.exposed.sql.Transaction

internal typealias TransactionAction = Transaction.() -> Unit

fun interface MainTransaction: (TransactionAction) -> Unit