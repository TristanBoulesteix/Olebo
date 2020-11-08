package jdr.exia.updater

import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.image.BufferedImage
import javax.swing.UIManager

private val trayIcon by lazy {
    val tray = SystemTray.getSystemTray()

    val icon = UIManager.getIcon("OptionPane.informationIcon")
    val image = BufferedImage(icon.iconWidth, icon.iconHeight, BufferedImage.TYPE_INT_RGB)
    icon.paintIcon(null, image.graphics, 0, 0)

    val trayIcon = TrayIcon(image, "Olebo updater").apply {
        this.isImageAutoSize = true
        this.toolTip = "Olebo updater is running"
        popupMenu = PopupMenu().apply {
            add(MenuItem("Test").apply {
                addActionListener {

                }
            })
        }
        addMouseListener(object : MouseAdapter() {
            private val frame = Frame().apply {
                isUndecorated = true
                type = Window.Type.UTILITY
                isResizable = false
                isVisible = true
            }

            override fun mouseClicked(e: MouseEvent) {
                frame.add(popupMenu)
                popupMenu.show(frame, e.xOnScreen, e.yOnScreen)
            }
        })
    }

    tray.add(trayIcon)

    return@lazy trayIcon
}

fun notify(title: String, message: String?, messageType: TrayIcon.MessageType = TrayIcon.MessageType.INFO) {
    if (SystemTray.isSupported())
        trayIcon.displayMessage(title, message, messageType)
}
