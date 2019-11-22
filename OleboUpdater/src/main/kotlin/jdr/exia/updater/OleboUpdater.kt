package jdr.exia.updater

import java.io.File
import java.io.InputStream
import javax.swing.JOptionPane
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    JOptionPane.showMessageDialog(null, "run")
    if(args.size != 2) exitProcess(-1)

    JOptionPane.showMessageDialog(null, "start")
    
    HttpUpdater().apply {
        update(this.getDownloadedFile(args[0]), File(args[1]))
    }.close()

    JOptionPane.showMessageDialog(null, "done")
}

fun update(stream: InputStream, jar: File) {
    stream.use {input ->
        jar.outputStream().use { output ->
            input.copyTo(output)
        }
    }
}