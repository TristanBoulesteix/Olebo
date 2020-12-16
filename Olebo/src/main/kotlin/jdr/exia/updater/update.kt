package jdr.exia.updater

import jdr.exia.OLEBO_VERSION
import jdr.exia.localization.*
import jdr.exia.model.dao.jarPath
import jdr.exia.model.dao.oleboUpdater
import jdr.exia.model.dao.option.Settings
import jdr.exia.utils.encodeQuotes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import javax.swing.JOptionPane

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

/**
 * Check for update on startup
 */
fun checkForUpdate() = GlobalScope.launch {
    lastRelease.let { release ->
        fun prepareUpdate(auto: Boolean = true) {
            Runtime.getRuntime().addShutdownHook(Thread {
                if ((auto && Settings.autoUpdate) || !auto) {
                    val url = release["assets"]!!.jsonArray[0].jsonObject["browser_download_url"].toString()
                    Runtime.getRuntime().exec("java -jar $oleboUpdater $url $jarPath ${UpdateOptions(localeCode = Settings.language.language).toString().encodeQuotes()}")
                }
            })
        }

        if (!release.isEmpty() && release["tag_name"].toString() != OLEBO_VERSION) {
            if (Settings.autoUpdate) {
                prepareUpdate()
            } else if (Settings.updateWarn != release["tag_name"].toString()) {
                withContext(Dispatchers.Main) {
                    val result = JOptionPane.showOptionDialog(
                        null,
                        Strings[ST_NEW_VERSION_AVAILABLE],
                        Strings[STR_UPDATE_AVAILABLE],
                        JOptionPane.YES_NO_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        arrayOf(Strings[STR_YES], Strings[STR_NO], Strings[ST_NEVER_ASK_UPDATE]),
                        Strings[STR_NO]
                    )
                    if (result == JOptionPane.YES_OPTION) {
                        prepareUpdate(false)
                        JOptionPane.showMessageDialog(
                            null,
                            Strings[ST_UPDATE_WILL_START_AT_SHUTDOWN],
                            Strings[STR_PREPARE_UPDATE],
                            JOptionPane.INFORMATION_MESSAGE
                        )
                    } else if (result == JOptionPane.CANCEL_OPTION) {
                        Settings.updateWarn = release["tag_name"].toString()
                    }
                }
            }
        }
    }
}