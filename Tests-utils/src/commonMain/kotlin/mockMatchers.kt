package fr.olebo.tests.utils

import dev.mokkery.matcher.ArgMatchersScope
import dev.mokkery.matcher.matching
import kotlinx.serialization.KSerializer
import kotlin.reflect.typeOf
import kotlinx.serialization.serializer as kotlinxSerializer

inline fun <reified T> ArgMatchersScope.serializer(): KSerializer<T> = matching(
    toString = { "serializer<${typeOf<T>()}>()" },
    predicate = {
        test<T>(it)
        kotlinxSerializer<T>().descriptor == it.descriptor
    }
)

inline fun <reified T> test(it: KSerializer<T>): Boolean {
    val aa = kotlinxSerializer<T>().descriptor
    val bb = it.descriptor
    val a = kotlinxSerializer<T>().descriptor == it.descriptor

    return a
}