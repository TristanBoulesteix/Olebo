package jdr.exia.view

import androidx.compose.desktop.ComposePanel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import jdr.exia.OLEBO_VERSION
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
import jdr.exia.view.element.ImageButtonBuilder
import jdr.exia.view.element.ScrollableColumn
import jdr.exia.view.menubar.FileMenu
import jdr.exia.view.tools.BorderBuilder
import jdr.exia.view.tools.DefaultFunction
import jdr.exia.view.tools.border
import jdr.exia.view.tools.withFocusCursor
import jdr.exia.view.ui.DIMENSION_MAIN_WINDOW
import jdr.exia.view.ui.blue
import jdr.exia.view.ui.setThemedContent
import jdr.exia.viewModel.home.*
import javax.swing.JMenuBar

@Suppress("FunctionName")
class HomeWindow : ComposableWindow("Olebo - ${StringLocale[STR_VERSION]} $OLEBO_VERSION") {
    private val viewModel = HomeViewModel()

    init {
        this.defaultCloseOperation = DISPOSE_ON_CLOSE

        DIMENSION_MAIN_WINDOW.let {
            this.size = it
            this.minimumSize = it
        }

        this.jMenuBar = JMenuBar().apply { add(FileMenu()) }

        this.contentPane = ComposePanel().apply {
            setThemedContent {
                MainContent(
                    acts = viewModel.acts,
                    content = viewModel.content,
                    switchContent = { viewModel.content = it },
                    onRowClick = viewModel::launchAct,
                    onDeleteAct = viewModel::deleteAct
                )
            }
        }

        this.setLocationRelativeTo(null)
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
            OutlinedButton(onClick = viewElements, modifier = Modifier.withFocusCursor()) {
                Text(text = StringLocale[STR_ELEMENTS])
            }
            OutlinedButton(onClick = startActCreation, modifier = Modifier.withFocusCursor()) {
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
                        onClick = {
                            onRowClick(act)
                            dispose()
                        },
                        buttonBuilders = listOf(
                            ImageButtonBuilder(content = imageFromIconRes("edit_icon"), onClick = { onEdit(act) }),
                            ImageButtonBuilder(content = imageFromIconRes("delete_icon"), onClick = { onDelete(act) })
                        )
                    )
                }
            }
        }
    }
}