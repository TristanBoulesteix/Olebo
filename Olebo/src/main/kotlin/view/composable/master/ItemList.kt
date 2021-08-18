@file:Suppress("FunctionName")

package jdr.exia.view.composable.master

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.useResource
import androidx.compose.ui.unit.dp
import jdr.exia.localization.STR_NO_ELEMENT
import jdr.exia.localization.STR_SEARCH
import jdr.exia.localization.StringLocale
import jdr.exia.model.element.Blueprint
import jdr.exia.model.element.Type
import jdr.exia.model.type.imageFromFile
import jdr.exia.view.element.ContentListRow
import jdr.exia.view.element.ScrollableColumn
import jdr.exia.view.element.builder.ImageButtonBuilder
import jdr.exia.view.tools.clickableWithCursor
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File

@Composable
fun ItemList(modifier: Modifier, items: Map<Type, List<Blueprint>>, createElement: (Blueprint) -> Unit) = Column(
    modifier = modifier.widthIn(max = 450.dp).fillMaxHeight().border(BorderStroke(1.dp, Color.Black))
) {
    var searchString by remember { mutableStateOf("") }

    OutlinedTextField(
        value = searchString,
        onValueChange = { searchString = it },
        modifier = Modifier.padding(10.dp).fillMaxWidth(),
        placeholder = { Text(text = StringLocale[STR_SEARCH]) },
        singleLine = true
    )

    ItemList(items = items, searchString = searchString, createElement = createElement)
}

@Composable
private fun ItemList(
    items: Map<Type, List<Blueprint>>,
    searchString: String,
    createElement: (Blueprint) -> Unit
) = ScrollableColumn {
    items.forEach { (type, list) ->
        ContentListRow(contentText = type.localizedName, modifier = Modifier.background(Color.Cyan))

        val filtered = transaction { list.filter { it.realName.contains(searchString, ignoreCase = true) } }

        if (filtered.isEmpty()) {
            ContentListRow(contentText = StringLocale[STR_NO_ELEMENT])
        } else {
            ColumnItem(items = filtered) {
                val name = transaction { it.realName }

                ContentListRow(
                    modifier = Modifier.clickableWithCursor { createElement(it) },
                    contentText = name,
                    buttonBuilders =
                    listOf(
                        ImageButtonBuilder(
                            if (type == Type.BASIC)
                                useResource("sprites/${it.sprite}", ::loadImageBitmap)
                            else
                                imageFromFile(File(it.sprite))
                        )
                    )
                )
            }
        }
    }
}