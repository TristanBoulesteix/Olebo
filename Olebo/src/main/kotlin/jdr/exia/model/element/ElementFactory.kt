package jdr.exia.model.element

import javax.swing.ImageIcon

object ElementFactory {
    fun buildElements(blueprints: List<Blueprint>): MutableList<Element> {
        val elements = mutableListOf<Element>()

        blueprints.forEach {
            when {
                it.type.getTypeWithTypeElement() == Type.OBJECT -> elements += Item(
                    it.name,
                    ImageIcon(it.sprite),
                    Position(0, 0),
                    true,
                    Size.S
                )
                it.type.getTypeWithTypeElement() == Type.PNJ -> elements += NonPlayableCharacter(
                    it.HP,
                    it.MP,
                    it.name,
                    ImageIcon(it.sprite),
                    Position(0, 0),
                    true,
                    Size.S
                )
                else -> elements += PlayableCharacter(
                    it.HP,
                    it.MP,
                    it.name,
                    ImageIcon(it.sprite),
                    Position(0, 0),
                    true,
                    Size.S
                )
            }
        }

        return elements
    }
}