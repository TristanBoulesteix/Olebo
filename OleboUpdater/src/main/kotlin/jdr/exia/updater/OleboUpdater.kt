package jdr.exia.updater

import java.io.File
import java.io.InputStream
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    if(args.size != 2) exitProcess(-1)
    
    HttpUpdater().apply {
        update(this.getDownloadedFile(args[0]), File(args[1]))
    }.close()
}

fun update(stream: InputStream, jar: File) {
    stream.use {input ->
        jar.outputStream().use { output ->
            input.copyTo(output)
        }
    }
}