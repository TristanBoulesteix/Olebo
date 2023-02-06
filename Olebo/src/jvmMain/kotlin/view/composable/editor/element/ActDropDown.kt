package jdr.exia.view.composable.editor.element

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import jdr.exia.model.act.Act
import jdr.exia.view.component.form.DropdownMenu
import org.jetbrains.exposed.sql.transactions.transaction

@Composable
fun ActDropDown(currentAct: Act?, onNewCurrentActSelected: (Act?) -> Unit) {
    val items = remember {
        listOf(ActItem.All) + transaction { Act.all().map(ActItem::Holder) }
    }

    val selectedActItem = remember(currentAct) { if (currentAct == null) ActItem.All else ActItem.Holder(currentAct) }

    DropdownMenu(
        items = items,
        selectedItem = selectedActItem,
        onItemSelected = {
            onNewCurrentActSelected(if (it is ActItem.Holder) it.act else null)
        },
        modifier = Modifier.padding(top = 10.dp)
    )
}

private sealed interface ActItem {
    object All : ActItem {
        override fun toString() = "All acts"
    }

    @JvmInline
    value class Holder(val act: Act) : ActItem {
        override fun toString() = transaction { act.name }
    }
}