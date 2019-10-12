package jdr.exia

import jdr.exia.controller.HomeFrameController
import jdr.exia.controller.ViewController
import jdr.exia.view.mainFrame.PlayerFrame
import java.awt.GraphicsEnvironment
import java.awt.Toolkit
import javax.swing.SwingUtilities
import javax.swing.UIManager

fun main() {
  ViewController
  val screens = GraphicsEnvironment.getLocalGraphicsEnvironment().screenDevices
  while (true){
    println(PlayerFrame.locationOnScreen.x)
    Thread.sleep(200)
    if(PlayerFrame.locationOnScreen.x < 0){
      println("a")

    }
  }
}