package jdr.exia.view.compose

import androidx.compose.desktop.ComposePanel
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
import androidx.compose.ui.unit.dp
import jdr.exia.OLEBO_VERSION
import jdr.exia.localization.STR_ADD_ACT
import jdr.exia.localization.STR_ELEMENTS
import jdr.exia.localization.STR_VERSION
import jdr.exia.localization.StringLocale
import jdr.exia.model.act.Act
import jdr.exia.model.tools.imageFromIconRes
import jdr.exia.view.compose.components.ContentListRow
import jdr.exia.view.compose.components.HeaderRow
import jdr.exia.view.compose.components.ImageButtonBuilder
import jdr.exia.view.compose.tools.BorderBuilder
import jdr.exia.view.compose.tools.DefaultFunction
import jdr.exia.view.compose.tools.border
import jdr.exia.view.compose.tools.withSetter
import jdr.exia.view.compose.ui.OleboTheme
import jdr.exia.view.compose.ui.blue
import jdr.exia.view.utils.components.FileMenu
import jdr.exia.viewModel.HomeViewModel
import java.awt.Dimension
import javax.swing.JFrame
import javax.swing.JMenuBar

@Suppress("FunctionName")
class HomeFrame : JFrame("Olebo - ${StringLocale[STR_VERSION]} $OLEBO_VERSION") {
    private val viewModel = HomeViewModel()

    init {
        this.defaultCloseOperation = DISPOSE_ON_CLOSE

        Dimension(700, 900).let {
            this.size = it
            this.minimumSize = it
        }

        this.jMenuBar = JMenuBar().apply { add(FileMenu()) }

        this.contentPane = ComposePanel().apply {
            setContent {
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
        }

        this.setLocationRelativeTo(null)
    }

    @Composable
    private fun MainContent(
        acts: List<Act>,
        refreshAct: DefaultFunction,
        onRowClick: (Act) -> Unit,
        onDeleteAct: (Act) -> Unit
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            var areElementsVisible by remember { mutableStateOf(false) }

            var editedActState by remember { mutableStateOf<Act?>(null) withSetter { newValue -> if (newValue == null) refreshAct() } }

            var actInCreation by remember { mutableStateOf(false) withSetter { newValue -> if (!newValue) refreshAct() } }

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
    private fun ActsView(
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
                            ImageButtonBuilder(content = imageFromIconRes("edit_icon"), onClick = { onEdit(act) }),
                            ImageButtonBuilder(content = imageFromIconRes("delete_icon"), onClick = { onDelete(act) })
                        )
                    )
                }
            }
        }
    }
}