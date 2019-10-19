package jdr.exia.model.utils

import jdr.exia.model.dao.DAO
import jdr.exia.model.dao.InstanceTable
import jdr.exia.model.element.Element

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
        DAO.saveElement(element)
        this.add(element)
    }
}

fun elementListOf(idScene: Int): MutableList<Element> = ElementList(idScene)