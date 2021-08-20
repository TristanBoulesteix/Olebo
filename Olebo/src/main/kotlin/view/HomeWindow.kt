package jdr.exia.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ApplicationScope
import jdr.exia.OLEBO_VERSION_NAME
import jdr.exia.localization.STR_ADD_ACT
import jdr.exia.localization.STR_ELEMENTS
import jdr.exia.localization.STR_VERSION
import jdr.exia.localization.StringLocale
import jdr.exia.model.act.Act
import jdr.exia.model.type.imageFromIconRes
import jdr.exia.view.composable.editor.ActEditorView
import jdr.exia.view.composable.editor.ElementsView
import jdr.exia.view.element.ContentListRow
import jdr.exia.view.element.HeaderRow
import jdr.exia.view.element.ScrollableColumn
import jdr.exia.view.element.builder.ImageButtonBuilder
import jdr.exia.view.menubar.FileMenu
import jdr.exia.view.tools.BorderBuilder
import jdr.exia.view.tools.DefaultFunction
import jdr.exia.view.tools.border
import jdr.exia.view.tools.withHandCursor
import jdr.exia.view.ui.HOME_WINDOWS_SIZE
import jdr.exia.view.ui.blue
import jdr.exia.viewModel.home.*
import javax.swing.JMenuBar

@Composable
fun ApplicationScope.HomeWindow(startAct: (Act) -> Unit) = Window(
    title = "Olebo - ${StringLocale[STR_VERSION]} $OLEBO_VERSION_NAME",
    size = HOME_WINDOWS_SIZE,
    minimumSize = HOME_WINDOWS_SIZE,
    menuBar = remember { JMenuBar().apply { add(FileMenu()) } }
) {
    val viewModel = remember { HomeViewModel() }

    MainContent(
        acts = viewModel.acts,
        content = viewModel.content,
        switchContent = { viewModel.content = it },
        onRowClick = startAct,
        onDeleteAct = viewModel::deleteAct
    )
}

@Composable
private fun MainContent(
    acts: List<Act>,
    content: HomeContent,
    switchContent: (HomeContent) -> Unit,
    onRowClick: (Act) -> Unit,
    onDeleteAct: (Act) -> Unit
) {
    Row(modifier = Modifier.fillMaxSize()) {
        when (content) {
            is ActsView -> ActsView(
                acts = acts,
                onRowClick = onRowClick,
                onEdit = { switchContent(ActEditor(it)) },
                onDelete = onDeleteAct,
                viewElements = { switchContent(ElementsView) },
                startActCreation = { switchContent(ActCreator) }
            )
            is ElementsView -> ElementsView(onDone = { switchContent(ActsView) })
            is ActEditor -> ActEditorView(act = content.act, onDone = { switchContent(ActsView) })
            is ActCreator -> ActEditorView(onDone = { switchContent(ActsView) })
        }
    }
}

@Composable
private fun ActsView(
    acts: List<Act>,
    onRowClick: (Act) -> Unit,
    onEdit: (Act) -> Unit,
    onDelete: (Act) -> Unit,
    viewElements: DefaultFunction,
    startActCreation: DefaultFunction
) = Column {
    HeaderRow {
        OutlinedButton(onClick = viewElements, modifier = Modifier.withHandCursor()) {
            Text(text = StringLocale[STR_ELEMENTS])
        }
        OutlinedButton(onClick = startActCreation, modifier = Modifier.withHandCursor()) {
            Text(text = StringLocale[STR_ADD_ACT])
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(blue).padding(15.dp)) {
        ScrollableColumn(
            modifier = Modifier.padding(20.dp).fillMaxSize().background(Color.White)
                .border(BorderBuilder.defaultBorder)
        ) {
            ColumnItem(items = acts) { act ->
                ContentListRow(
                    contentText = act.name,
                    onClick = { onRowClick(act) },
                    buttonBuilders = listOf(
                        ImageButtonBuilder(content = imageFromIconRes("edit_icon"), onClick = { onEdit(act) }),
                        ImageButtonBuilder(content = imageFromIconRes("delete_icon"), onClick = { onDelete(act) })
                    )
                )
            }
        }
    }
}