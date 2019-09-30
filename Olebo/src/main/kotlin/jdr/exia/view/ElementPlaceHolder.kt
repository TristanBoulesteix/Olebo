package jdr.exia.view

import java.awt.Image
import java.awt.Rectangle
import javax.imageio.ImageIO

object ElementPlaceHolder {
    private var sprite: Image = ImageIO.read(ElementPlaceHolder::class.java.getResource(".png").openStream())

    var x: Int = 0
    var y: Int = 0
    var hitbox: Rectangle? = null

    init {
        this.x = 50
        this.y = 50
        this.hitbox = Rectangle(x, y, 32, 32)
    }
}
