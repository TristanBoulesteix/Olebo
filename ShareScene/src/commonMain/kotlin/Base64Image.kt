package fr.olebo.sharescene

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@Serializable
@JvmInline
value class Base64Image(internal val value: String) {
    val cssBase64ImageCode
        get() = "data:image/jpeg;base64,$value"
}