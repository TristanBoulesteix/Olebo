package jdr.exia.model.tools

import jdr.exia.model.act.Scene
import jdr.exia.model.command.CommandManager
import jdr.exia.model.element.Blueprint
import jdr.exia.model.element.Element
import jdr.exia.model.element.TypeElement
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/**
 * Check if element is a PNJ or a PJ
 *
 * @return true if it's a character
 */
@OptIn(ExperimentalContracts::class)
fun Element?.isCharacter(): Boolean {
    contract {
        returns(true) implies (this@isCharacter != null)
    }

    return this != null && (this.type == TypeElement.PNJ || this.type == TypeElement.PJ)
}

/**
 * Check if blueprint is a PNJ or a PJ
 *
 * @return true if it's a character
 */
@OptIn(ExperimentalContracts::class)
fun Blueprint?.isCharacter(): Boolean {
    contract {
        returns(true) implies (this@isCharacter != null)
    }

    val blueprint = this@isCharacter

    return transaction { blueprint != null && (blueprint.type == TypeElement.PNJ || blueprint.type == TypeElement.PJ) }
}

fun Blueprint.getLifeOrNull() = if (isCharacter()) HP else null

fun Blueprint.getManaOrNull() = if (isCharacter()) MP else null

inline fun <T> Scene?.callCommandManager(
    elementWithData: Map<Element, T>,
    func: (Map<Element, T>, CommandManager) -> Unit
) =
    this?.let { scene ->
        func(elementWithData, CommandManager(scene.id))
    }