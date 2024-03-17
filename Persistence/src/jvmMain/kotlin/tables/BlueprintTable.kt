package fr.olebo.persistence.tables

import fr.olebo.domain.models.Configurations
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.insertIgnore

internal object BlueprintTable : IntIdTable(), Initializable {
    val name = varchar("name", 50)
    val sprite = varchar("sprite", 200)
    val HP = integer("HP").nullable()
    val MP = integer("MP").nullable()
    val idType = reference("id_type", TypeTable)

    override fun initialize(configurations: Configurations) {
        // Pointers
        BlueprintTable.insertIgnore {
            it[name] = "@pointerTransparent"
            it[sprite] = "pointer_transparent.png"
            it[idType] = EntityID(4, TypeTable)
        }

        BlueprintTable.insertIgnore {
            it[name] = "@pointerBlue"
            it[sprite] = "pointer_blue.png"
            it[idType] = EntityID(4, TypeTable)
        }


        BlueprintTable.insertIgnore {
            it[name] = "@pointerWhite"
            it[sprite] = "pointer_white.png"
            it[idType] = EntityID(4, TypeTable)
        }

        BlueprintTable.insertIgnore {
            it[name] = "@pointerGreen"
            it[sprite] = "pointer_green.png"
            it[idType] = EntityID(4, TypeTable)
        }
    }
}
