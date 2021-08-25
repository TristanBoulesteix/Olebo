package jdr.exia.model.tools

import jdr.exia.model.act.Scene
import jdr.exia.model.command.CommandManager
import jdr.exia.model.element.Blueprint
import jdr.exia.model.element.Element
import jdr.exia.model.element.Type
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/**
 * Convert a String to the corresponding boolean
 *
 * @return true if the value is "true"
 */
fun String?.toBoolean(): Boolean = this?.lowercase() == "true"

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

    return this != null && (this.type == Type.PNJ || this.type == Type.PJ)
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

    return transaction { blueprint != null && (blueprint.type == Type.PNJ || blueprint.type == Type.PJ) }
}

inline fun Scene?.callCommandManager(elements: List<Element>, func: (CommandManager, List<Element>) -> Unit) =
    this?.let { scene ->
        func(CommandManager(scene.id.value), elements)
    }

inline fun <T> Scene?.callCommandManager(
    elementWithData: Map<Element, T>,
    func: (Map<Element, T>, CommandManager) -> Unit
) =
    this?.let { scene ->
        func(elementWithData, CommandManager(scene.id.value))
    }