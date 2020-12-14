package jdr.exia.updater

import jdr.exia.localization.*
import java.awt.TrayIcon
import java.io.File
import java.io.InputStream
import java.net.BindException
import java.net.InetAddress
import java.net.ServerSocket
import java.util.*
import kotlin.system.exitProcess

const val UPDATER_VERSION = "1.2.0"

var locale: Locale = Locale.ENGLISH

fun main(vararg args: String) {
    Strings(::locale)

    try {
        ServerSocket(9999, 0, InetAddress.getByName(null))
    } catch (b: BindException) {
        exitProcess(-2)
    }.apply {
        if (args.size == 1) {
            println(UPDATER_VERSION)
            return
        }

        if (args.size !in 2..3) exitProcess(-1)

        if(args.size == 3) {
            locale = Locale(args[2])
            Strings(::locale)
        }

        try {
            HttpUpdater().use {
                notify(Strings[ST_OLEBO_IS_UPDATING], Strings[ST_NOT_TURN_OFF])
                update(it.getDownloadedFile(args[0]), File(args[1]))
            }
            notify(Strings[ST_UPDATE_SUCCESS], null)
        } catch (e: Exception) {
            e.printStackTrace()
            notify(Strings[ST_UPDATE_FAILED], Strings[ST_UPDATE_TRY_AGAIN], TrayIcon.MessageType.ERROR)

            Thread.sleep(3000)
            exitProcess(-1)
        }

        Thread.sleep(5000)
        exitProcess(0)
    }
}

fun update(stream: InputStream, jar: File) {
    val tempJar = File.createTempFile("olebo", null)
    stream.use { input ->
        tempJar.outputStream().use { output ->
            input.copyTo(output)
        }
    }

    menuItem.isSafeToStop = false

    tempJar.inputStream().use { input ->
        jar.outputStream().use { output ->
            input.copyTo(output)
        }
    }

    tempJar.deleteOnExit()
}
