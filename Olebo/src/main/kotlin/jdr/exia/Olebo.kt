package jdr.exia

import jdr.exia.view.homeFrame.HomeFrame
import org.apache.http.HttpEntity
import org.apache.http.HttpResponse
import org.apache.http.client.config.CookieSpecs
import org.apache.http.client.config.RequestConfig
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.InputStream
import java.net.UnknownHostException
import javax.swing.SwingUtilities
import javax.swing.UIManager


const val VERSION = "1.0-BETA"

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
private fun checkForUpdate() = Thread(Runnable {
    HttpClientUpdater().apply {
        val release = this.lastRelease
        if (!release.isEmpty && release["tag_name"] != VERSION) {
            update(this.getDownloadedFile(((release["assets"] as JSONArray)[0] as JSONObject)["browser_download_url"] as String))
        }
    }.close()
}).run()

/**
 * Update the program
 *
 * @param stream The stream of the jar to get
 */
private fun update(stream: InputStream) {
    val jar = File(::update::class.java.protectionDomain.codeSource.location.toURI())
    stream.use {input ->
        jar.outputStream().use { output ->
            input.copyTo(output)
        }
    }
}

/**
 * Check all releases on <a href="github.com">Github</a>
 */
class HttpClientUpdater {
    private val httpClient by lazy { HttpClients.createDefault() }
    private val downloadClient by lazy {
        HttpClients.custom().setDefaultRequestConfig(RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build()).build()
    }

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
        } catch (e: UnknownHostException) {
            JSONObject()
        }

    /**
     * Get the exectuable file on Github from url
     *
     * @param url The url of the file
     *
     * @return The output stream of the file
     */
    fun getDownloadedFile(url: String): InputStream {
        downloadClient.apply {
            val request = HttpGet(url)
            val response: HttpResponse = this.execute(request)
            val entity: HttpEntity = response.entity
            //val responseCode: Int = response.statusLine.statusCode
            //println("Response Code: $responseCode")
            return entity.content
            /*          val out = FileOutputStream(File(filePath))
                      var inByte: Int
                      while (stream.read().also { inByte = it } != -1) {
                          out.write(inByte)
                      }
                      stream.close()
                      out.close()*/
        }
    }

    /**
     * Close the connection
     */
    fun close() {
        httpClient.close()
        downloadClient.close()
    }
}
