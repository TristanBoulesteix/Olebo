package jdr.exia.utils

import jdr.exia.VERSION
import jdr.exia.model.dao.Settings
import jdr.exia.model.utils.jarPath
import jdr.exia.model.utils.oleboUpdater
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import org.json.JSONArray
import org.json.JSONObject
import javax.swing.JOptionPane

/**
 * Check all releases on <a href="github.com">Github</a>
 */
object HttpClientUpdater {
    /**
     * Return a JSONObject with datas of the last release
     */
    private val lastRelease: JSONObject
        get() = try {
            val request = HttpGet("https://api.github.com/repos/TristanBoulesteix/Olebo/releases")

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
        val release = this@HttpClientUpdater.lastRelease

        fun prepareUpdate(auto: Boolean = true) {
            Runtime.getRuntime().addShutdownHook(Thread {
                if ((auto && Settings.autoUpdate) || !auto) {
                    val url = ((release["assets"] as JSONArray)[0] as JSONObject)["browser_download_url"] as String
                    Runtime.getRuntime().exec("javam -jar $oleboUpdater $url $jarPath")
                }
            })
        }

        if (!release.isEmpty && release["tag_name"] != VERSION) {
            if (Settings.autoUpdate) {
                prepareUpdate()
            } else if (Settings.updateWarn != release["tag_name"]) {
                withContext(Dispatchers.Main) {
                    val result = JOptionPane.showOptionDialog(null, "Une mise à jour de Olebo est disponible. Voulez-vous l\'installer ?", "Mise à jour disponible", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, arrayOf("Oui", "Non", "Non, ne plus demander pour cette version"), "Non")
                    if (result == JOptionPane.YES_OPTION) {
                        prepareUpdate(false)
                        JOptionPane.showMessageDialog(null, "La mise à jour aura lieu lors de la fermeture de l'application.", "Préparation de la mise à jour", JOptionPane.INFORMATION_MESSAGE)
                    } else if (result == JOptionPane.CANCEL_OPTION) {
                        Settings.updateWarn = release["tag_name"].toString()
                    }
                }
            }
        }
    }
}