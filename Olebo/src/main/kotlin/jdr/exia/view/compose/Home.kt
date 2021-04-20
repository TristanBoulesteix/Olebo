@file:Suppress("FunctionName")

package jdr.exia.view.compose

import androidx.compose.desktop.DesktopMaterialTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import jdr.exia.OLEBO_VERSION
import jdr.exia.localization.STR_ADD_ACT
import jdr.exia.localization.STR_ELEMENTS
import jdr.exia.localization.STR_VERSION
import jdr.exia.localization.StringLocale
import jdr.exia.model.act.Act
import jdr.exia.model.tools.imageFromIconRes
import jdr.exia.view.compose.components.ButtonBuilder
import jdr.exia.view.compose.components.ContentListRow
import jdr.exia.view.compose.components.CustomWindow
import jdr.exia.view.compose.components.HeaderRow
import jdr.exia.view.compose.tools.BorderBuilder
import jdr.exia.view.compose.tools.DefaultFunction
import jdr.exia.view.compose.tools.border
import jdr.exia.view.compose.tools.withSetter
import jdr.exia.view.compose.ui.OleboTheme
import jdr.exia.view.compose.ui.blue
import jdr.exia.view.utils.components.FileMenu
import jdr.exia.viewModel.HomeViewModel
import javax.swing.JMenuBar

val defaultWindowSize = IntSize(700, 900)

fun showHomeWindow() = CustomWindow(
    title = "Olebo - ${StringLocale[STR_VERSION]} $OLEBO_VERSION",
    size = defaultWindowSize,
    minimumSize = defaultWindowSize,
    jMenuBar = JMenuBar().apply { add(FileMenu()) }
) {
    val viewModel = HomeViewModel()

    OleboTheme {
        DesktopMaterialTheme {
            MainContent(
                acts = viewModel.acts,
                refreshAct = viewModel::refreshActs,
                onRowClick = viewModel::launchAct,
                onDeleteAct = viewModel::deleteAct
            )
        }
    }
}

@Composable
fun MainContent(
    acts: List<Act>,
    refreshAct: DefaultFunction,
    onRowClick: (Act) -> Unit,
    onDeleteAct: (Act) -> Unit
) {
    Row(modifier = Modifier.fillMaxSize()) {
        var areElementsVisible by remember { mutableStateOf(false) }

        var editedActState by remember { mutableStateOf<Act?>(null) withSetter { if (it == null) refreshAct() } }

        var actInCreation by remember { mutableStateOf(false) withSetter { if (!it) refreshAct() } }

        when {
            areElementsVisible -> ElementsView(onDone = { areElementsVisible = false })
            editedActState != null -> ActEditorView(act = editedActState, onDone = { editedActState = null })
            actInCreation -> ActEditorView(onDone = { actInCreation = false })
            else -> ActsView(
                acts = acts,
                onRowClick = onRowClick,
                onEdit = { editedActState = it },
                onDelete = onDeleteAct,
                viewElements = { areElementsVisible = true },
                startActCreation = { actInCreation = true }
            )
        }
    }
}

@Composable
fun ActsView(
    acts: List<Act>,
    onRowClick: (Act) -> Unit,
    onEdit: (Act) -> Unit,
    onDelete: (Act) -> Unit,
    viewElements: DefaultFunction,
    startActCreation: DefaultFunction
) = Column {
    HeaderRow {
        OutlinedButton(onClick = viewElements) {
            Text(text = StringLocale[STR_ELEMENTS])
        }
        OutlinedButton(onClick = startActCreation) {
            Text(text = StringLocale[STR_ADD_ACT])
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(blue).padding(15.dp)) {
        LazyColumn(
            modifier = Modifier.padding(20.dp).fillMaxSize().background(Color.White)
                .border(BorderBuilder.defaultBorder)
        ) {
            items(items = acts) { act ->
                ContentListRow(
                    contentText = act.name,
                    onClick = { onRowClick(act) },
                    buttonBuilders = listOf(
                        ButtonBuilder(imageFromIconRes("edit_icon")) { onEdit(act) },
                        ButtonBuilder(imageFromIconRes("delete_icon")) { onDelete(act) }
                    )
                )
            }
        }
    }
}