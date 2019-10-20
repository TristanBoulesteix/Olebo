package jdr.exia.model.utils

import jdr.exia.model.dao.DAO
import jdr.exia.model.dao.InstanceTable
import jdr.exia.model.element.Element
import jdr.exia.model.element.Item
import jdr.exia.model.element.Position
import jdr.exia.model.element.Size
import javax.swing.ImageIcon

/**
 * The instance list of all element in a scene
 *
 * @param idScene the scene id
 */
class ElementList(idScene: Int) : ArrayList<Element>() {
    init {
        this += DAO.getElementsWithIdScene(idScene)
    }

    /**
     * Remove an element from the list and the database
     */
    override fun remove(element: Element): Boolean {
        DAO.deleteWithId(element.idInstance, InstanceTable)
        return super.remove(element)
    }

    /**
     * Add a new element to the database
     */
    operator fun plusAssign(element: Element) {
        DAO.saveNewElement(element)
        this.add(element)
    }
}

fun elementListOf(idScene: Int): ElementList = ElementList(idScene)

fun main() {
    val a = elementListOf(1)
    println(a)
    val b = Item(
        1,
        "this.name",
        ImageIcon("this.sprite"),
        Position(2, 0),
        true,
        Size.S,
        1,
        1
    )
    a += b
    println(a)
    b.setPosition(2, 2).also { b.commit() }
    println(b.idInstance)
    a.remove(b)
    println(a)
}