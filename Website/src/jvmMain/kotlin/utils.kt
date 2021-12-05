package fr.olebo

import fr.olebo.sharescene.Connection
import fr.olebo.sharescene.ShareSceneSession
import java.util.*

fun synchronizedSessionSet() = synchronizedSet<ShareSceneSession>()

fun synchronizedConnectionsSet() = synchronizedSet<Connection>()

fun <T> synchronizedSet(): MutableSet<T> = Collections.synchronizedSet(LinkedHashSet())