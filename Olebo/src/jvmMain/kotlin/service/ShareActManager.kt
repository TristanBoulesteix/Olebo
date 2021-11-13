package jdr.exia.service

import io.ktor.client.*
import java.io.Closeable

class ShareActManager private constructor(private val client: HttpClient) : Closeable by client {


    companion object {
        operator fun invoke() = ShareActManager(client)
    }
}