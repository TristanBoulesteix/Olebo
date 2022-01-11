package fr.olebo.sharescene.map

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@Serializable
@JvmInline
value class Base64Image(internal val value: String)