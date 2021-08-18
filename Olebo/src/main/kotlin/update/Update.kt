package jdr.exia.update

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.cio.*
import io.ktor.utils.io.*
import jdr.exia.OLEBO_VERSION_CODE
import jdr.exia.system.OLEBO_DIRECTORY
import jdr.exia.system.OS
import java.io.File

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

    if (response.status.isSuccess()) {
        return response.receive<Release>().takeIf { it.versionId > OLEBO_VERSION_CODE }
    }

    return null
}

suspend fun getInstallerExecutable(onUpdateProgress: (Long) -> Unit): Result<File> {
    val fileToWrite = File("$OLEBO_DIRECTORY${File.separator}olebo_updater.exe")

    val os = OS.current

    val response = try {
        client.get<HttpResponse>("${SERVER_URL}releases/last/download") {
            parameter("os", os.name)

            onDownload { bytesSentTotal, contentLength ->
                val percentage = (bytesSentTotal / contentLength) * 100
                onUpdateProgress(percentage)
            }
        }
    } catch (e: Exception) {
        return Result.failure(e)
    }

    if (response.status.isSuccess()) {
        response.content.copyAndClose(fileToWrite.writeChannel())
    }

    return Result.success(fileToWrite)
}