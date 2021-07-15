package jdr.exia.update
import jdr.exia.system.OS
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import kotlin.system.exitProcess

private const val URL = "https://api.github.com/repos/TristanBoulesteix/Olebo/releases"

private val json = Json { ignoreUnknownKeys = true }

private val lastRelease
    get() = try {
        val request = HttpGet(URL)

        request.setHeader("Content-type", "application/json")

        HttpClients.createDefault().use {
            it.execute(request).use { response ->
                json.decodeFromString<List<Release>>(EntityUtils.toString(response.entity))
                    .firstOrNull()
            }
        }
    } catch (e: Exception) {
        null
    }

val currentChangeLogs
    get() = lastRelease?.body

suspend fun getUpdaterForCurrentOsAsync(currentOleboVersion: String) = coroutineScope {
    async {
        Updater(
            lastRelease.takeIf {
                it?.tag != currentOleboVersion
            }, OS.current
        )
    }
}

@OptIn(DelicateCoroutinesApi::class)
fun forceUpdate(exitCode: Int = 0): Nothing {
    Updater(lastRelease, OS.current)?.startUpdate { true }
    exitProcess(exitCode)
}