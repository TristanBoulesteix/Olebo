package jdr.exia.viewModel.tags

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import jdr.exia.model.dao.TagTable
import jdr.exia.model.element.Tag
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction

private typealias Tags = MutableList<String>

class ElementTagHolder {
    private val tagsInDatabase = transaction { Tag.all().map(Tag::value) }

    private val tagsToDelete: Tags = mutableStateListOf()

    private val tagsToCreate: Tags = mutableStateListOf()

    val tags by derivedStateOf {
        val list = (tagsInDatabase + tagsToCreate) - tagsToDelete
        list.toSet()
    }

    fun createTags(tags: List<String>) {
        tagsToCreate += tags
    }

    fun deleteTags(tags: List<String>) {
        tagsToDelete += tags
        tagsToCreate -= tags.toSet()
    }

    /**
     * Save tag changes to database
     *
     * @return A set containing all deleted tags
     */
    fun pushToDatabase(): Set<String> {
        tagsToCreate.toSet().forEach {
            if (it !in tagsInDatabase)
                transaction { Tag.newFrom(it) }
        }

        val setToDelete = tagsToDelete.toSet()

        setToDelete.forEach { tag ->
            if (tag !in tagsToCreate)
                transaction { TagTable.deleteWhere { TagTable.id eq tag } }
        }

        return setToDelete
    }
}