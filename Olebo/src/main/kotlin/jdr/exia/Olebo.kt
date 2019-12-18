package jdr.exia

import jdr.exia.model.utils.jarPath
import jdr.exia.model.utils.oleboUpdater
import jdr.exia.view.homeFrame.HomeFrame
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import org.json.JSONArray
import org.json.JSONObject
import javax.swing.SwingUtilities
import javax.swing.UIManager

const val VERSION = "1.0.1-BETA"

fun main() {
    checkForUpdate()

    SwingUtilities.invokeLater {
        UIManager.setLookAndFeel(
            UIManager.getSystemLookAndFeelClassName()
        )

        HomeFrame().isVisible = true
    }
}

/**
 * Check for update on startup
 */
private fun checkForUpdate() = Thread {
    HttpClientUpdater().apply {
        val release = this.lastRelease
        if (!release.isEmpty && release["tag_name"] != VERSION) {
            Runtime.getRuntime().addShutdownHook(Thread {
                val url = ((release["assets"] as JSONArray)[0] as JSONObject)["browser_download_url"] as String
                Runtime.getRuntime().exec("java -jar $oleboUpdater $url $jarPath")
            })
        }
    }.close()
}.start()


/**
 * Check all releases on <a href="github.com">Github</a>
 */
class HttpClientUpdater {
    private val httpClient by lazy { HttpClients.createDefault() }

    /**
     * Return a JSONObject with datas of the last release
     */
    val lastRelease: JSONObject
        get() = try {
            val request = HttpGet("https://api.github.com/repos/TristanBoulesteix/Olebo/releases")

            request.setHeader("Content-type", "application/json")

            httpClient.execute(request).use { response ->
                // Get HttpResponse Status
                //println(response.statusLine.toString())
                val entity = response.entity
                val result = EntityUtils.toString(entity)
                (JSONArray(result).first() as JSONObject)
            }
        } catch (e: Exception) {
            JSONObject()
        }

    /**
     * Close the connection
     */
    fun close() = httpClient.close()
}
