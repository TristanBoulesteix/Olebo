package jdr.exia.update

import jdr.exia.OLEBO_VERSION
import jdr.exia.localization.*
import jdr.exia.model.dao.jarPath
import jdr.exia.model.dao.oleboUpdater
import jdr.exia.model.dao.option.Settings
import kotlinx.coroutines.*
import kotlinx.serialization.json.*
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import javax.swing.JOptionPane
import kotlin.system.exitProcess

private const val URL = "https://api.github.com/repos/TristanBoulesteix/Olebo/releases"

private val lastRelease: JsonObject
    get() = try {
        val request = HttpGet(URL)

        request.setHeader("Content-type", "application/json")

        HttpClients.createDefault().use {
            it.execute(request).use { response ->
                // Get HttpResponse Status
                //println(response.statusLine.toString())
                val result = EntityUtils.toString(response.entity)
                Json.parseToJsonElement(result).jsonArray.first().jsonObject
            }
        }
    } catch (e: Exception) {
        JsonObject(emptyMap())
    }

val currentChangelogs
    get() = lastRelease["body"]?.jsonPrimitive?.contentOrNull

/**
 * Check for update on startup
 */
@OptIn(DelicateCoroutinesApi::class)
fun checkForUpdate() = GlobalScope.launch {
    lastRelease.takeIf { it.isNotEmpty() }?.let { release ->
        fun prepareUpdate(auto: Boolean = true) {
            Settings.wasJustUpdated = true

            Runtime.getRuntime().addShutdownHook(Thread {
                if ((auto && Settings.autoUpdate) || !auto) {
                    val url = release["assets"]!!.jsonArray[0].jsonObject["browser_download_url"].toString()
                    runJar(
                        oleboUpdater, url, jarPath, UpdateOptions(
                            !auto,
                            Settings.language.language
                        ).toQuotedString()
                    )
                }
            })
        }

        if (!release.isEmpty() && release["tag_name"]!!.jsonPrimitive.content != OLEBO_VERSION) {
            if (Settings.autoUpdate) {
                prepareUpdate()
            } else if (Settings.updateWarn != release["tag_name"]?.jsonPrimitive?.content) {
                withContext(Dispatchers.Main) {
                    val result = JOptionPane.showOptionDialog(
                        null,
                        StringLocale[ST_NEW_VERSION_AVAILABLE],
                        StringLocale[STR_UPDATE_AVAILABLE],
                        JOptionPane.YES_NO_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        arrayOf(StringLocale[STR_YES], StringLocale[STR_NO], StringLocale[ST_NEVER_ASK_UPDATE]),
                        StringLocale[STR_NO]
                    )
                    if (result == JOptionPane.YES_OPTION) {
                        JOptionPane.showMessageDialog(
                            null,
                            StringLocale[ST_UPDATE_OLEBO_RESTART],
                            StringLocale[STR_PREPARE_UPDATE],
                            JOptionPane.INFORMATION_MESSAGE
                        )
                        prepareUpdate(false)
                        exitProcess(0)
                    } else if (result == JOptionPane.CANCEL_OPTION) {
                        Settings.updateWarn = release["tag_name"]!!.jsonPrimitive.content
                    }
                }
            }
        }
    }
}

/**
 * Update Olebo without prompt and restart it
 */
fun forceUpdateAndRestart(exitCode: Int = 0): Nothing {
    val release = lastRelease
    val url = release["assets"]!!.jsonArray[0].jsonObject["browser_download_url"].toString()
    Settings.wasJustUpdated = true
    runJar(oleboUpdater, url, jarPath, UpdateOptions(true).toQuotedString())
    exitProcess(exitCode)
}