package jdr.exia.update

import fr.olebo.utils.Result
import fr.olebo.utils.onSuccess
import fr.olebo.utils.onThrow
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.*
import io.ktor.util.cio.*
import io.ktor.utils.io.*
import jdr.exia.OLEBO_VERSION_CODE
import jdr.exia.model.dao.option.Preferences
import jdr.exia.service.client
import jdr.exia.system.OLEBO_DIRECTORY
import jdr.exia.system.OS
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Checks for updates based on the provided release code.
 *
 * @param releaseCode The release code to check for updates. If null, the method will check for the latest release.
 * @return A Result object representing the result of the update check. If an update is available, the Result will
 * contain the Release object representing the update. If there was an error during the update check, the Result will
 * contain the Throwable object representing the error.
 */
suspend fun checkForUpdate(releaseCode: Int? = null): Result<Release, Int?> {
    val response = try {
        client.use { it.get("${Preferences.oleboUrl.value}/releases/" + (releaseCode ?: "last")).body<HttpResponse>() }
    } catch (e: CancellationException) {
        throw e
    } catch (e: Throwable) {
        return Result.exception(e)
    }

    if (response.status.isSuccess()) {
        return response.body<Release>().takeIf {
            it.versionId > OLEBO_VERSION_CODE
        }?.let { Result.success(it) }
            ?: Result.failure(null)
    }

    return Result.failure(response.status.value)
}

/**
 * Retrieves the installer executable file for a given version code.
 *
 * @param versionCode The version code of the installer to retrieve.
 * @param onUpdateProgress A callback function to report the progress of the download.
 *                         It receives a Long value representing the current progress in percentage.
 *
 * @return A Result object indicating the success or failure of the operation.
 *         On success, the Result object contains the File representing the installer executable.
 *         On failure, the Result object contains an Exception indicating the cause of the failure.
 */
@OptIn(InternalAPI::class)
suspend fun getInstallerExecutable(versionCode: Int, onUpdateProgress: (Long) -> Unit): Result<File, String> {
    val fileToWrite = File("$OLEBO_DIRECTORY${File.separator}olebo_updater.exe")

    val os = OS.current

    val response = try {
        client.use {
            it.get("${Preferences.oleboUrl.value}/releases/$versionCode/download") {
                parameter("os", os.name)
                onDownload { bytesSentTotal, contentLength ->
                    val percentage = (bytesSentTotal / contentLength) * 100
                    onUpdateProgress(percentage)
                }
            }
                .body<HttpResponse>()
        }
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        return Result.exception(e)
    }

    if (response.status.isSuccess()) {
        // Not working !!!
        // response.bodyAsChannel().copyAndClose(fileToWrite.writeChannel())
        response.content.copyAndClose(fileToWrite.writeChannel(Dispatchers.IO))
    } else return Result.exception(IllegalStateException("Response status : ${response.status.value}"))

    return Result.success(fileToWrite)
}

/**
 * Downloads update installer and exits the application.
 *
 * @param versionCode The code of the update version.
 * @param onFinishDownload The callback function to be invoked when the download is completed.
 * @param onProgressUpdate Callback function to be invoked when the progress of the download is updated.
 * @param onDownloadSuccess Callback function to be invoked when the download is successful.
 * @param onDownloadFailure Callback function to be invoked when the download fails.
 *
 */
suspend fun downloadUpdateAndExit(
    versionCode: Int,
    onFinishDownload: () -> Unit,
    onProgressUpdate: (Long) -> Unit = {},
    onDownloadSuccess: () -> Unit = {},
    onDownloadFailure: (Throwable) -> Unit = {}
): Unit = withContext(Dispatchers.IO) {
    getInstallerExecutable(onUpdateProgress = onProgressUpdate, versionCode = versionCode).onSuccess {
        onDownloadSuccess()

        Preferences.versionUpdatedTo = versionCode

        Preferences.incrementAttemptForVersion(versionCode)

        // Run executable installer generated by Inno Setup
        Runtime.getRuntime().addShutdownHook(Thread {
            @Suppress("BlockingMethodInNonBlockingContext")
            ProcessBuilder(it.absolutePath, "/SP-", "/silent", "/noicons").start()
        })

        onFinishDownload()
    }.onThrow(onDownloadFailure)
}