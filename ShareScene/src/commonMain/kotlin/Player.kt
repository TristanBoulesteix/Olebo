package fr.olebo.sharescene

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@Serializable
@JvmInline
value class Player(val name: String)
