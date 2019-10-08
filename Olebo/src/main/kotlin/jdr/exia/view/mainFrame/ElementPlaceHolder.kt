package jdr.exia.view.mainFrame

import org.w3c.dom.css.Rect
import java.awt.Image
import java.awt.Rectangle
import javax.imageio.ImageIO

    class ElementPlaceHolder(public var x: Int = 0,public var y: Int = 0, public val sprite: Image, public var hitbox: Rectangle) {
        //private val sprite: Image = ImageIO.read(ElementPlaceHolder::class.java.getResource(".png").openStream())




        init {
            this.x = 50
            this.y = 50
            this.hitbox = Rectangle(x,y,32,32)
        }
    }
