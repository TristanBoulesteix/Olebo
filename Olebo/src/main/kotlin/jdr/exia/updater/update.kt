package jdr.exia.updater

import jdr.exia.OLEBO_VERSION
import jdr.exia.localization.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import jdr.exia.model.dao.jarPath
import jdr.exia.model.dao.oleboUpdater
import jdr.exia.model.dao.option.Settings
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import org.json.JSONArray
import org.json.JSONObject
import javax.swing.JOptionPane

private const val URL = "https://api.github.com/repos/TristanBoulesteix/Olebo/releases"

private val lastRelease: JSONObject
    get() = try {
        val request = HttpGet(URL)

        request.setHeader("Content-type", "application/json")

        HttpClients.createDefault().use {
            it.execute(request).use { response ->
                // Get HttpResponse Status
                //println(response.statusLine.toString())
                val result = EntityUtils.toString(response.entity)
                (JSONArray(result).first() as JSONObject)
            }
        }
    } catch (e: Exception) {
        JSONObject()
    }

/**
 * Check for update on startup
 */
fun checkForUpdate() = GlobalScope.launch {
    lastRelease.let { release ->
        fun prepareUpdate(auto: Boolean = true) {
            Runtime.getRuntime().addShutdownHook(Thread {
                if ((auto && Settings.autoUpdate) || !auto) {
                    val url = ((release["assets"] as JSONArray)[0] as JSONObject)["browser_download_url"] as String
                    Runtime.getRuntime().exec("java -jar $oleboUpdater $url $jarPath ${Settings.language.language}")
                }
            })
        }

        if (!release.isEmpty && release["tag_name"] != OLEBO_VERSION) {
            if (Settings.autoUpdate) {
                prepareUpdate()
            } else if (Settings.updateWarn != release["tag_name"]) {
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