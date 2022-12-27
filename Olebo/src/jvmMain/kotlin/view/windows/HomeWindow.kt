package jdr.exia.view.windows

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.ApplicationScope
import jdr.exia.OLEBO_VERSION_NAME
import jdr.exia.localization.*
import jdr.exia.model.act.Act
import jdr.exia.view.component.ContentListRow
import jdr.exia.view.component.HeaderRow
import jdr.exia.view.component.LazyScrollableColumn
import jdr.exia.view.component.builder.IconButtonBuilder
import jdr.exia.view.composable.editor.ElementsView
import jdr.exia.view.composable.editor.act.ActEditorView
import jdr.exia.view.menubar.MainMenuBar
import jdr.exia.view.tools.BorderBuilder
import jdr.exia.view.tools.toBorderStroke
import jdr.exia.view.ui.HOME_WINDOWS_SIZE
import jdr.exia.viewModel.home.*

@Composable
fun ApplicationScope.HomeWindow(startAct: (Act) -> Unit) = Window(
    title = "Olebo - ${StringLocale[STR_VERSION]} $OLEBO_VERSION_NAME",
    size = HOME_WINDOWS_SIZE,
    minimumSize = HOME_WINDOWS_SIZE
) {
    MainMenuBar(::exitApplication)

    val viewModel = remember(::HomeViewModel)

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
) = Box(modifier = Modifier.fillMaxSize()) {
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

@Composable
private fun ActsView(
    acts: List<Act>,
    onRowClick: (Act) -> Unit,
    onEdit: (Act) -> Unit,
    onDelete: (Act) -> Unit,
    viewElements: () -> Unit,
    startActCreation: () -> Unit
) = Column {
    HeaderRow {
        OutlinedButton(onClick = viewElements) {
            Text(text = StringLocale[STR_ELEMENTS])
        }
        OutlinedButton(onClick = startActCreation) {
            Text(text = StringLocale[STR_ADD_ACT])
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colors.secondaryVariant).padding(15.dp)) {
        Card(
            modifier = Modifier.padding(20.dp).fillMaxSize(),
            border = BorderBuilder.defaultBorder.toBorderStroke()
        ) {
            when {
                acts.isNotEmpty() -> {
                    LazyScrollableColumn {
                        items(items = acts, key = { it.id }) { act ->
                            ContentListRow(
                                contentText = act.name,
                                contentTooltip = StringLocale[STR_OPEN_ACT_TOOLTIP],
                                onClick = { onRowClick(act) },
                                buttonBuilders = listOf(
                                    IconButtonBuilder(
                                        content = Icons.Outlined.Edit,
                                        tooltip = StringLocale[STR_EDIT_ACT_TOOLTIP],
                                        onClick = { onEdit(act) }
                                    ),
                                    IconButtonBuilder(
                                        content = Icons.Outlined.Delete,
                                        tooltip = StringLocale[STR_DELETE_ACT],
                                        onClick = { onDelete(act) }
                                    )
                                )
                            )
                        }
                    }
                }

                else -> Column(verticalArrangement = Arrangement.Center) {
                    Text(
                        text = StringLocale[STR_NO_ACT],
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        fontWeight = FontWeight.Bold,
                        fontSize = 30.sp
                    )
                    OutlinedButton(
                        onClick = startActCreation,
                        content = { Text(StringLocale[STR_ADD_ACT]) },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
        }
    }
}