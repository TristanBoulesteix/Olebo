package jdr.exia.updater

import org.apache.http.HttpEntity
import org.apache.http.HttpResponse
import org.apache.http.client.config.CookieSpecs
import org.apache.http.client.config.RequestConfig
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClients
import java.io.Closeable
import java.io.InputStream

/**
 * Check all releases on <a href="github.com">Github</a>
 */
class HttpUpdater : Closeable {
    private val downloadClient by lazy {
        HttpClients.custom().setDefaultRequestConfig(RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build())
            .build()
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
        }
    }

    /**
     * Close the connection
     */
    override fun close() = downloadClient.close()
}