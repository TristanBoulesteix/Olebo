package jdr.exia.utils

import jdr.exia.VERSION
import jdr.exia.model.utils.jarPath
import jdr.exia.model.utils.oleboUpdater
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import org.json.JSONArray
import org.json.JSONObject

/**
 * Check all releases on <a href="github.com">Github</a>
 */
object HttpClientUpdater {
    private val httpClient by lazy { HttpClients.createDefault() }

    /**
     * Return a JSONObject with datas of the last release
     */
    private val lastRelease: JSONObject
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

    var autoUpdate = true

    /**
     * Check for update on startup
     */
    fun checkForUpdate() = Thread {
        val release = this.lastRelease
        if (!release.isEmpty && release["tag_name"] != VERSION) {
            Runtime.getRuntime().addShutdownHook(Thread {
                val url = ((release["assets"] as JSONArray)[0] as JSONObject)["browser_download_url"] as String
                Runtime.getRuntime().exec("java -jar $oleboUpdater $url $jarPath")
            })
        }
        this.close()
    }.start()

    /**
     * Close the connection
     */
    private fun close() = httpClient.close()
}