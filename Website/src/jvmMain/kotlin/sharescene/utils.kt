package fr.olebo.sharescene

import java.util.*

fun synchronizedSessionSet() = synchronizedSet<ShareSceneSession>()

fun synchronizedConnectionsSet() = synchronizedSet<Connection>()

fun <T> synchronizedSet(): MutableSet<T> = Collections.synchronizedSet(LinkedHashSet())