import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils

private const val URL = "https://api.github.com/repos/TristanBoulesteix/Olebo/releases"

private val lastRelease
    get() = try {
        val request = HttpGet(URL)

        request.setHeader("Content-type", "application/json")

        HttpClients.createDefault().use {
            it.execute(request).use { response ->
                Json { ignoreUnknownKeys = true }.decodeFromString<List<Release>>(EntityUtils.toString(response.entity))
                    .firstOrNull()
            }
        }
    } catch (e: Exception) {
        null
    }

suspend fun getReleaseManagerAsync(currentOleboVersion: String) = coroutineScope {
    async { UpdateManager(lastRelease.takeIf { it?.tag != currentOleboVersion }) }
}