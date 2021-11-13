package jdr.exia.service

import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.X509TrustManager

const val SERVER_URL = "https://olebo.fr/"

/**
 * This is a certificate manager to allow Olebo to access to the API without verifying its certificate.
 *
 * TODO: Remove this when the certificate for olebo.fr is fixed
 */
private object TrustAllX509TrustManager : X509TrustManager {
    override fun getAcceptedIssuers(): Array<X509Certificate> = emptyArray()

    override fun checkClientTrusted(certs: Array<X509Certificate?>?, authType: String?) = Unit

    override fun checkServerTrusted(certs: Array<X509Certificate?>?, authType: String?) = Unit
}

val client
    get() = HttpClient(Apache) {
        engine {
            sslContext = SSLContext.getInstance("TLS").apply {
                init(null, arrayOf(TrustAllX509TrustManager), SecureRandom())
            }
        }
        install(JsonFeature) {
            serializer = KotlinxSerializer()
        }
    }