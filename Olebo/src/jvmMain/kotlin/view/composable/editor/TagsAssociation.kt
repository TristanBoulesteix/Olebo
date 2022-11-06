package jdr.exia.view.composable.editor

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import jdr.exia.localization.*
import jdr.exia.model.dao.BlueprintTagTable
import jdr.exia.view.component.dialog.ConfirmMessage
import jdr.exia.view.component.form.AutocompleteList
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

@Composable
fun TagsAssociation(
    nameOfAssociated: String,
    selection: List<String>,
    tags: Iterable<String>,
    onConfirm: (newTags: List<String>, tagsToDelete: List<String>, selectedTags: List<String>) -> Unit
) = Column(Modifier.fillMaxSize(.8f), horizontalAlignment = Alignment.CenterHorizontally) {
    Text(
        text = buildAnnotatedString {
            append(StringLocale[STR_MANAGE_TAGS])

            withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                append(nameOfAssociated)
            }
        },
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(10.dp).padding(top = 5.dp)
    )

    val newSuggestions: MutableList<String> = remember(::mutableStateListOf)

    val tagsToDelete: MutableList<String> = remember(::mutableStateListOf)

    val suggestions by remember {
        derivedStateOf {
            (newSuggestions + tags) - tagsToDelete.toSet()
        }
    }

    val selectionAsState = remember(selection) { selection.toMutableStateList() }

    var confirmDelete by remember { mutableStateOf<TagToDeleteInfo?>(null) }

    AutocompleteList(
        modifier = Modifier.padding(10.dp).padding(end = 5.dp).fillMaxWidth().weight(.9f),
        suggestionsList = suggestions,
        selectedItems = selectionAsState,
        onItemChecked = { value, isChecked ->
            val index = suggestions.indexOf(value).takeIf { it >= 0 } ?: return@AutocompleteList

            if (isChecked) {
                selectionAsState += suggestions[index]
            } else {
                selectionAsState -= value
            }
        },
        onItemCreated = {
            selectionAsState += it
            newSuggestions.add(0, it)
        },
        placeholder = StringLocale[STR_SEARCH_CREATE_TAG],
        tooltipMessage = StringLocale[ST_TOOLTIP_TAGS],
        onItemDeleted = {
            transaction {
                confirmDelete =
                    TagToDeleteInfo(it, BlueprintTagTable.select { BlueprintTagTable.tag eq it }.count())
            }
        }
    )

    if (confirmDelete != null) {
        val (tagToDelete, numberOfOccurrences) = confirmDelete!!

        val delete = { tagsToDelete += tagToDelete }

        if (numberOfOccurrences != 0L) ConfirmMessage(
            message = StringLocale[ST_STR1_INT2_CONFIRM_DELETE_TAG, tagToDelete, numberOfOccurrences],
            title = StringLocale[STR_CONFIRM_DELETE_TAG_TITLE],
            onCloseRequest = { confirmDelete = null },
            onConfirm = delete,
            doubleCheck = false
        ) else SideEffect(delete)
    }

    Box(Modifier.fillMaxWidth().weight(.1f), contentAlignment = Alignment.Center) {
        Button(
            onClick = { onConfirm(newSuggestions, tagsToDelete, selectionAsState) },
            content = { Text(StringLocale[STR_CLOSE_VALIDATE]) }
        )
    }
}

@Immutable
private data class TagToDeleteInfo(val name: String, val occurrences: Long)