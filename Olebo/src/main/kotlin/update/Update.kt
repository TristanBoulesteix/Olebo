package jdr.exia.update

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.apache.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.cio.*
import io.ktor.utils.io.*
import jdr.exia.OLEBO_VERSION_CODE
import jdr.exia.model.dao.option.Settings
import jdr.exia.system.OLEBO_DIRECTORY
import jdr.exia.system.OS
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.io.File
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.X509TrustManager

private const val SERVER_URL = "https://olebo.fr/"

/**
 * This is a certificate manager to allow Olebo to access to the API without verifying its certificate.
 *
 * TODO: Remove this when the certificate for olebo.fr is fixed
 */
private class TrustAllX509TrustManager : X509TrustManager {
    override fun getAcceptedIssuers(): Array<X509Certificate> = emptyArray()

    override fun checkClientTrusted(certs: Array<X509Certificate?>?, authType: String?) = Unit

    override fun checkServerTrusted(certs: Array<X509Certificate?>?, authType: String?) = Unit
}

private val client
    get() = HttpClient(Apache) {
        engine {
            sslContext = SSLContext.getInstance("TLS").apply {
                init(null, arrayOf(TrustAllX509TrustManager()), SecureRandom())
            }
        }
        install(JsonFeature) {
            serializer = KotlinxSerializer()
        }
    }

suspend fun checkForUpdate(): Result<Release> {
    val response = try {
        client.use { it.get<HttpResponse>("${SERVER_URL}releases/last") }
    } catch (e: Throwable) {
        return Result.failure(e)
    }

    if (response.status.isSuccess()) {
        return response.receive<Release>().takeIf { it.versionId > OLEBO_VERSION_CODE }?.let { Result.success(it) }
            ?: Result.failure(Throwable())
    }

    return Result.failure(Throwable())
}

suspend fun getInstallerExecutable(onUpdateProgress: (Long) -> Unit): Result<File> {
    val fileToWrite = File("$OLEBO_DIRECTORY${File.separator}olebo_updater.exe")

    val os = OS.current

    val response = try {
        client.use {
            it.get<HttpResponse>("${SERVER_URL}releases/last/download") {
                parameter("os", os.name)

                onDownload { bytesSentTotal, contentLength ->
                    val percentage = (bytesSentTotal / contentLength) * 100
                    onUpdateProgress(percentage)
                }
            }
        }
    } catch (e: Exception) {
        return Result.failure(e)
    }

    if (response.status.isSuccess()) {
        response.content.copyAndClose(fileToWrite.writeChannel())
    } else return Result.failure(IllegalStateException("Response status : ${response.status.value}"))

    return Result.success(fileToWrite)
}

suspend fun downloadAndExit(
    onExitSuccess: () -> Unit,
    onProgressUpdate: (Long) -> Unit = {},
    onDownloadSuccess: () -> Unit = {},
    onDownloadFailure: (Throwable) -> Unit = {}
) = coroutineScope {
    this.launch(Dispatchers.IO) {
        getInstallerExecutable(onUpdateProgress = onProgressUpdate).onSuccess {
            onDownloadSuccess()

            Settings.wasJustUpdated = true

            // Run executable installer generated by Inno Setup
            Runtime.getRuntime().addShutdownHook(Thread {
                ProcessBuilder(it.absolutePath, "/SP-", "/silent", "/noicons").start()
            })

            onExitSuccess()
        }.onFailure(onDownloadFailure)
    }
}