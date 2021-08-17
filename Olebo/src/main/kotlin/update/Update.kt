package jdr.exia.update

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import jdr.exia.OLEBO_VERSION_CODE

private const val SERVER_URL = "http://localhost:8080/"

private val client = HttpClient {
    install(JsonFeature) {
        serializer = KotlinxSerializer()
    }
}

suspend fun checkForUpdate(): Release? {
    val response = try {
        client.get<HttpResponse>("${SERVER_URL}releases/last")
    } catch (e: Exception) {
        return null
    }

    if (response.status.value in 200..299) {
        return response.receive<Release>().takeIf { it.versionId > OLEBO_VERSION_CODE }
    }

    return null
}
