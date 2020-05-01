package jdr.exia.updater

import java.awt.SystemTray
import java.awt.TrayIcon
import java.awt.image.BufferedImage
import java.io.File
import javax.swing.UIManager

private val trayIcon by lazy {
    val tray = SystemTray.getSystemTray()

    val icon = UIManager.getIcon("OptionPane.informationIcon")
    val image = BufferedImage(icon.iconWidth, icon.iconHeight, BufferedImage.TYPE_INT_RGB)
    icon.paintIcon(null, image.graphics, 0, 0)

    val trayIcon = TrayIcon(image, "Olebo updater").apply {
        this.isImageAutoSize = true
        this.toolTip = "Olebo updater is running"
    }

    tray.add(trayIcon)

    return@lazy trayIcon
}

fun notify(title: String, message: String?, messageType: TrayIcon.MessageType = TrayIcon.MessageType.INFO) {
    if (SystemTray.isSupported())
        trayIcon.displayMessage(title, message, messageType)
}
