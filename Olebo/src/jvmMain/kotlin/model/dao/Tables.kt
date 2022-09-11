package jdr.exia.model.dao

import jdr.exia.OLEBO_VERSION_CODE
import jdr.exia.model.dao.option.SerializableColor
import jdr.exia.model.dao.option.SerializableLabelState
import jdr.exia.model.element.Layer
import jdr.exia.model.element.SizeElement
import jdr.exia.model.element.TypeElement
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import java.util.*

/**
 * List of all tables in the database
 * Put them in order of initialization
 */
val tables
    get() = arrayOf(
        SettingsTable,
        ActTable,
        SceneTable,
        TypeTable,
        BlueprintTable,
        LayerTable,
        SizeTable,
        InstanceTable,
        TagTable,
        BlueprintTagTable
    )


sealed interface Initializable {
    fun initialize()
}

sealed class EnumInitializable<E : Enum<E>>(private val values: Array<E>) : IntIdTable(), Initializable {
    abstract val enumValue: Column<E>

    final override fun initialize() {
        values.forEachIndexed { index, enum ->
            val idEnum = index + 1

            if (select { (id eq idEnum) and (enumValue eq enum) }.count() <= 0) {
                insert {
                    it[id] = EntityID(idEnum, this@EnumInitializable)
                    it[enumValue] = enum
                }
            }
        }
    }
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
    private const val CHANGELOGS_VERSION = "changelogs_version"
    const val SHOULD_OPEN_PLAYER_WINDOW_IN_FULL_SCREEN = "player_window_in_full_screen"

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
        insertOptionIfNotExists(12, SHOULD_OPEN_PLAYER_WINDOW_IN_FULL_SCREEN, true, insertOnlyIfNotExists)
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
    val scene = reference("id_scene", SceneTable).default(EntityID(0, SceneTable))
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

object TypeTable : EnumInitializable<TypeElement>(enumValues()) {
    override val enumValue = enumerationByName<TypeElement>("type", 50)
}

object LayerTable : EnumInitializable<Layer>(enumValues()) {
    override val enumValue = enumeration<Layer>("layer")
}

object InstanceTable : IntIdTable() {
    val currentHP = integer("current_HP").nullable()
    val currentMP = integer("current_MP").nullable()
    val x = float("x").default(10f)
    val y = float("y").default(10f)
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

object SizeTable : EnumInitializable<SizeElement>(enumValues()) {
    override val enumValue = enumerationByName<SizeElement>("size", 50)
}

object TagTable : IdTable<String>() {
    override val id =  varchar("tagValue", 40).entityId()

    override val primaryKey = PrimaryKey(id)
}

object BlueprintTagTable : Table() {
    val blueprint = reference("blueprint", BlueprintTable)
    val tag = reference("tag", TagTable)

    override val primaryKey = PrimaryKey(blueprint, tag)
}