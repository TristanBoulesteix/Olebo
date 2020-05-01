package jdr.exia.updater

import java.awt.TrayIcon
import java.io.File
import java.io.InputStream
import kotlin.system.exitProcess


const val VERSION = "1.0.1"

fun main(args: Array<String>) {
    if (args.size == 1) println(VERSION)

    if (args.size != 2) exitProcess(-1)

    try {
        HttpUpdater().apply {
            notify("Olebo est en train de se mettre à jour", "N'éteignez pas votre ordinateur")
            update(this.getDownloadedFile(args[0]), File(args[1]))
        }.close()
        notify("Olebo a bien été mis à jour", null)
    } catch (e: Exception) {
        e.printStackTrace()
        notify("Impossible de mettre à jour Olebo", "Veuillez réessayer ultérieurement", TrayIcon.MessageType.ERROR)

        Thread.sleep(2000)
        exitProcess(-1)
    }

    Thread.sleep(2000)
    exitProcess(0)
}

fun update(stream: InputStream, jar: File) {
    val tempJar = File.createTempFile("olebo", null)
    stream.use { input ->
        tempJar.outputStream().use { output ->
            input.copyTo(output)
        }
    }

    tempJar.inputStream().use { input ->
        jar.outputStream().use { output ->
            input.copyTo(output)
        }
    }

    tempJar.deleteOnExit()
}
