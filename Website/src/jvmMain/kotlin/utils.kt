package fr.olebo

import fr.olebo.sharescene.Connection
import fr.olebo.sharescene.ShareSceneSession
import java.util.*

fun synchronizedSessionSet(): MutableSet<ShareSceneSession> = Collections.synchronizedSet(LinkedHashSet())

fun synchronizedConnectionsSet(): MutableSet<Connection> = Collections.synchronizedSet(LinkedHashSet())