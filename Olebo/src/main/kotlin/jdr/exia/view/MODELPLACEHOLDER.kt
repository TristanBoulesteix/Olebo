package jdr.exia.view

import java.awt.Image

object MODELPLACEHOLDER {
    private val elements: Array<ElementPlaceHolder> = arrayOf() // the array containing the different sprites to display

    init {
        elements[0] = ElementPlaceHolder
    }

    fun toSprites(): Array<Image>? { // Turns an array of elements to a corresponding
        val length = elements.size

        return null
    }
}
