import java.awt.TrayIcon
import java.io.File
import java.io.InputStream
import java.net.BindException
import java.net.InetAddress
import java.net.ServerSocket
import kotlin.system.exitProcess

const val VERSION = "1.1.0"

fun main(args: Array<String>) {
    try {
        ServerSocket(9999, 0, InetAddress.getByName(null))
    } catch (b: BindException) {
        exitProcess(-2)
    }.apply {
        if (args.size == 1) {
            println(VERSION)
            return
        }

        if (args.size != 2) exitProcess(-1)

        try {
            HttpUpdater().use {
                notify("Olebo est en train de se mettre à jour", "N'éteignez pas votre ordinateur")
                update(it.getDownloadedFile(args[0]), File(args[1]))
            }
            notify("Olebo a bien été mis à jour", null)
        } catch (e: Exception) {
            e.printStackTrace()
            notify("Impossible de mettre à jour Olebo", "Veuillez réessayer ultérieurement", TrayIcon.MessageType.ERROR)

            Thread.sleep(3000)
            exitProcess(-1)
        }

        Thread.sleep(2000)
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
