@file:Suppress("FunctionName")

package jdr.exia.view.compose

import androidx.compose.desktop.DesktopMaterialTheme
import androidx.compose.desktop.WindowEvents
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
import jdr.exia.model.utils.imageFromIconRes
import jdr.exia.view.compose.components.ButtonBuilder
import jdr.exia.view.compose.components.ContentRow
import jdr.exia.view.compose.components.CustomWindow
import jdr.exia.view.compose.tools.BorderInlined
import jdr.exia.view.compose.tools.DefaultFunction
import jdr.exia.view.compose.tools.border
import jdr.exia.view.compose.ui.OleboTheme
import jdr.exia.view.compose.ui.blue
import jdr.exia.view.compose.ui.lightOrange
import jdr.exia.view.utils.components.FileMenu
import jdr.exia.viewModel.HomeViewModel
import javax.swing.JMenuBar

val defaultWindowSize = IntSize(700, 900)

var homeWindowSize by mutableStateOf(defaultWindowSize)
    private set

fun showHomeWindow() = CustomWindow(
    title = "Olebo - ${StringLocale[STR_VERSION]} $OLEBO_VERSION",
    size = defaultWindowSize,
    minimumSize = defaultWindowSize,
    jMenuBar = JMenuBar().apply { add(FileMenu()) },
    events = WindowEvents(
        onOpen = { homeWindowSize = defaultWindowSize },
        onResize = { homeWindowSize = it }
    )
) {
    val viewModel = HomeViewModel()

    OleboTheme {
        DesktopMaterialTheme {
            MainContent(
                acts = viewModel.acts,
                onRowClick = viewModel::launchAct,
                onDeleteAct = viewModel::deleteAct
            )
        }
    }
}

@Composable
fun MainContent(
    acts: List<Act>,
    onRowClick: (Act) -> Unit,
    onDeleteAct: (Act) -> Unit
) {
    Row(modifier = Modifier.fillMaxSize()) {
        var editedActState by remember { mutableStateOf<Act?>(null) }

        var actInCreation by remember { mutableStateOf(false) }

        when {
            editedActState != null -> ActEditorView(act = editedActState, onDone = { editedActState = null })
            actInCreation -> ActEditorView(onDone = { actInCreation = false })
            else -> ActsView(
                acts = acts,
                onRowClick = onRowClick,
                onEdit = { editedActState = it },
                onDelete = onDeleteAct,
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
    startActCreation: DefaultFunction
) {
    Column {
        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier.fillMaxWidth().background(lightOrange).padding(15.dp)
        ) {
            OutlinedButton(onClick = { println("Add element") }) {
                Text(text = StringLocale[STR_ELEMENTS])
            }
            OutlinedButton(onClick = startActCreation) {
                Text(text = StringLocale[STR_ADD_ACT])
            }
        }

        Box(modifier = Modifier.fillMaxSize().background(blue).padding(15.dp)) {
            LazyColumn(
                modifier = Modifier.padding(20.dp).fillMaxSize().background(Color.White)
                    .border(BorderInlined.defaultBorder)
            ) {
                items(items = acts) { act ->
                    ContentRow(
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
}