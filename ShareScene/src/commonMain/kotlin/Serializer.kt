package fr.olebo.sharescene

import kotlinx.serialization.KSerializer

expect class Id

internal expect object IdSerializer : KSerializer<Id>