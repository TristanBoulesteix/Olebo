package jdr.exia.model.dao

import jdr.exia.OLEBO_VERSION_CODE
import jdr.exia.model.dao.option.SerializableColor
import jdr.exia.model.dao.option.SerializableLabelState
import jdr.exia.model.element.Layer
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import java.util.*

/**
 * List of all tables in the database
 * Put them in order of initialization
 */
val tables by lazy {
    arrayOf(
        SettingsTable,
        ActTable,
        SceneTable,
        TypeTable,
        BlueprintTable,
        LayerTable,
        SizeTable,
        InstanceTable,
    )
}

sealed interface Initializable {
    fun initialize()
}

/**
 * Table where the database infos are stored. This table must be initialized before the others in order to check database version.
 */
object BaseInfo : Table(), Initializable {
    private const val BASE_VERSION = "base_version"

    private val keyInfo = varchar("key_info", 50)
    val value = varchar("value", 50)

    override val primaryKey = PrimaryKey(keyInfo)

    val versionBase
        get() = BaseInfo.select(keyInfo eq BASE_VERSION).firstOrNull()?.getOrNull(value)
            ?.toIntOrNull()

    override fun initialize() {
        if (BaseInfo.select(keyInfo eq BASE_VERSION).count() <= 0) {
            BaseInfo.insert {
                it[keyInfo] = BASE_VERSION
                it[value] = OLEBO_VERSION_CODE.toString()
            }
        } else {
            BaseInfo.update({ keyInfo eq BASE_VERSION }) { it[value] = OLEBO_VERSION_CODE.toString() }
        }
    }
}

/**
 * Table where user settings are stored.
 */
object SettingsTable : IntIdTable(), Initializable {
    const val AUTO_UPDATE = "autoUpdate"
    const val UPDATE_WARN = "updateWarn"
    const val CURSOR_ENABLED = "cursorEnabled"
    const val CURRENT_LANGUAGE = "current_language"
    const val CURSOR_COLOR = "cursor_color"
    const val PLAYER_FRAME_ENABLED = "PlayerFrame_enabled"
    const val DEFAULT_ELEMENT_VISIBILITY = "default_element_visibility"
    const val LABEL_STATE = "label_enabled"
    const val LABEL_COLOR = "label_color"
    const val CHANGELOGS_VERSION = "changelogs_version"

    val name = varchar("name", 255)
    val value = varchar("value", 255).default("")

    override fun initialize() = initializeDefault(true)

    fun initializeDefault(insertOnlyIfNotExists: Boolean = false) {
        insertOptionIfNotExists(2, AUTO_UPDATE, true, insertOnlyIfNotExists)
        insertOptionIfNotExists(3, UPDATE_WARN, "", insertOnlyIfNotExists)
        insertOptionIfNotExists(4, CURSOR_ENABLED, true, insertOnlyIfNotExists)
        insertOptionIfNotExists(5, CURRENT_LANGUAGE, Locale.getDefault().language, insertOnlyIfNotExists)
        insertOptionIfNotExists(6, CURSOR_COLOR, "", insertOnlyIfNotExists)
        insertOptionIfNotExists(7, PLAYER_FRAME_ENABLED, false, insertOnlyIfNotExists)
        insertOptionIfNotExists(8, DEFAULT_ELEMENT_VISIBILITY, false, insertOnlyIfNotExists)
        insertOptionIfNotExists(9, LABEL_STATE, SerializableLabelState.ONLY_FOR_MASTER.encode(), insertOnlyIfNotExists)
        insertOptionIfNotExists(10, LABEL_COLOR, SerializableColor.BLACK.encode(), insertOnlyIfNotExists)
        insertOptionIfNotExists(11, CHANGELOGS_VERSION, "", insertOnlyIfNotExists)
    }

    private fun insertOptionIfNotExists(id: Int, name: String, value: Any, insertOnlyIfNotExists: Boolean) {
        fun insertOrUpdate(builder: UpdateBuilder<Int>) {
            builder[SettingsTable.id] = EntityID(id, SettingsTable)
            builder[SettingsTable.name] = name
            builder[SettingsTable.value] = value.toString()
        }

        if (!insertOnlyIfNotExists) {
            SettingsTable.update({ SettingsTable.id eq id }) {
                insertOrUpdate(it)
            }
        } else if (SettingsTable.select((SettingsTable.id eq id) and (SettingsTable.name eq name)).count() <= 0) {
            SettingsTable.insert {
                insertOrUpdate(it)
            }
        }
    }
}

object ActTable : IntIdTable() {
    val name = varchar("name", 50)
    val idScene = integer("id_scene").references(SceneTable.id).default(0)
}

object SceneTable : IntIdTable() {
    val name = varchar("name", 50)
    val background = varchar("background", 200)
    val idAct = integer("id_act").references(ActTable.id)
}

object BlueprintTable : IntIdTable(), Initializable {
    val name = varchar("name", 50)
    val sprite = varchar("sprite", 200)
    val HP = integer("HP").nullable()
    val MP = integer("MP").nullable()
    val idType = reference("id_type", TypeTable)

    override fun initialize() {
        // Pointers
        if (BlueprintTable.select((idType eq 4) and (name eq "@pointerTransparent") and (sprite eq "pointer_transparent.png"))
                .count() <= 0
        ) {
            BlueprintTable.insert {
                it[name] = "@pointerTransparent"
                it[sprite] = "pointer_transparent.png"
                it[idType] = EntityID(4, TypeTable)
            }
        }

        if (BlueprintTable.select((idType eq 4) and (name eq "@pointerBlue") and (sprite eq "pointer_blue.png"))
                .count() <= 0
        ) {
            BlueprintTable.insert {
                it[name] = "@pointerBlue"
                it[sprite] = "pointer_blue.png"
                it[idType] = EntityID(4, TypeTable)
            }
        }

        if (BlueprintTable.select((idType eq 4) and (name eq "@pointerWhite") and (sprite eq "pointer_white.png"))
                .count() <= 0
        ) {
            BlueprintTable.insert {
                it[name] = "@pointerWhite"
                it[sprite] = "pointer_white.png"
                it[idType] = EntityID(4, TypeTable)
            }
        }

        if (BlueprintTable.select((idType eq 4) and (name eq "@pointerGreen") and (sprite eq "pointer_green.png"))
                .count() <= 0
        ) {
            BlueprintTable.insert {
                it[name] = "@pointerGreen"
                it[sprite] = "pointer_green.png"
                it[idType] = EntityID(4, TypeTable)
            }
        }
    }
}

object TypeTable : IntIdTable(), Initializable {
    val name = varchar("type", 50)

    override fun initialize() {
        if (TypeTable.select((id eq 1) and (name eq "Object")).count() <= 0) {
            TypeTable.insert {
                it[id] = EntityID(1, TypeTable)
                it[name] = "Object"
            }
        }

        if (TypeTable.select((id eq 2) and (name eq "PJ")).count() <= 0) {
            TypeTable.insert {
                it[id] = EntityID(2, TypeTable)
                it[name] = "PJ"
            }
        }

        if (TypeTable.select((id eq 3) and (name eq "PNJ")).count() <= 0) {
            TypeTable.insert {
                it[id] = EntityID(3, TypeTable)
                it[name] = "PNJ"
            }
        }

        if (TypeTable.select((id eq 4) and (name eq "Basic")).count() <= 0) {
            TypeTable.insert {
                it[id] = EntityID(4, TypeTable)
                it[name] = "Basic"
            }
        }
    }
}

object LayerTable : IntIdTable(), Initializable {
    val layerValue = enumeration<Layer>("layer")

    override fun initialize() {
        enumValues<Layer>().forEachIndexed { index, layer ->
            val idLayer = index + 1
            if (select { (id eq idLayer) and (layerValue eq layer) }.count() <= 0) {
                insert {
                    it[id] = idLayer
                    it[layerValue] = layer
                }
            }
        }
    }
}

object InstanceTable : IntIdTable() {
    val currentHP = integer("current_HP").nullable()
    val currentMP = integer("current_MP").nullable()
    val x = integer("x").default(10)
    val y = integer("y").default(10)
    val idSize = reference("ID_Size", SizeTable, onDelete = ReferenceOption.CASCADE).default(EntityID(2, SizeTable))
    val visible = bool("Visible").default(false)
    val orientation = float("Orientation").default(0f)
    val layer =
        reference("id_priority", LayerTable, onDelete = ReferenceOption.CASCADE).default(EntityID(2, LayerTable))
    val idScene = integer("ID_Scene").references(SceneTable.id).default(0)
    val idBlueprint = integer("id_blueprint").references(BlueprintTable.id).default(0)
    val deleted = bool("deleted").default(false)
    val alias = varchar("alias", 255).default("")
}

object SizeTable : IntIdTable(), Initializable {
    val size = varchar("Size", 10)
    val value = integer("Value")

    override fun initialize() {
        if (SizeTable.select((id eq 1) and (size eq "XS") and (value eq 30)).count() <= 0) {
            SizeTable.insert {
                it[id] = EntityID(1, SizeTable)
                it[size] = "XS"
                it[value] = 30
            }
        }

        if (SizeTable.select((id eq 2) and (size eq "S") and (value eq 60)).count() <= 0) {
            SizeTable.insert {
                it[id] = EntityID(2, SizeTable)
                it[size] = "S"
                it[value] = 60
            }
        }

        if (SizeTable.select((id eq 3) and (size eq "M") and (value eq 120)).count() <= 0) {
            SizeTable.insert {
                it[id] = EntityID(3, SizeTable)
                it[size] = "M"
                it[value] = 120
            }
        }

        if (SizeTable.select((id eq 4) and (size eq "L") and (value eq 200)).count() <= 0) {
            SizeTable.insert {
                it[id] = EntityID(4, TypeTable)
                it[size] = "L"
                it[value] = 200
            }
        }

        if (SizeTable.select((id eq 5) and (size eq "XL") and (value eq 300)).count() <= 0) {
            SizeTable.insert {
                it[id] = EntityID(5, TypeTable)
                it[size] = "XL"
                it[value] = 300
            }
        }

        if (SizeTable.select((id eq 6) and (size eq "XXL") and (value eq 400)).count() <= 0) {
            SizeTable.insert {
                it[id] = EntityID(6, TypeTable)
                it[size] = "XXL"
                it[value] = 400
            }
        }
    }
}

private inline fun <reified T : Enum<T>> Table.enumeration(name: String): Column<T> = enumeration(name, T::class)