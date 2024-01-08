package jdr.exia.viewModel.holder

import jdr.exia.model.element.Blueprint
import jdr.exia.model.element.TypeElement

typealias TypedBlueprints = Map<TypeElement, List<Blueprint>>

fun TypedBlueprints?.orEmptyValues(): TypedBlueprints = this ?: TypeElement.entries.associateWith { emptyList() }