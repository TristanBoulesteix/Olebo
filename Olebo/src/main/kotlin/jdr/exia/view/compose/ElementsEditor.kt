@file:Suppress("FunctionName")

package jdr.exia.view.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import jdr.exia.localization.ST_SCENE_ALREADY_EXISTS_OR_INVALID
import jdr.exia.localization.ST_UNKNOWN_ERROR
import jdr.exia.localization.StringLocale
import jdr.exia.model.element.Blueprint
import jdr.exia.model.element.Type
import jdr.exia.model.tools.imageFromIconRes
import jdr.exia.model.utils.Result
import jdr.exia.view.compose.components.*
import jdr.exia.view.compose.tools.BorderInlined
import jdr.exia.view.compose.tools.DefaultFunction
import jdr.exia.view.compose.tools.applyIf
import jdr.exia.view.compose.tools.border
import jdr.exia.view.compose.ui.blue
import jdr.exia.view.utils.MessageType
import jdr.exia.view.utils.showMessage
import jdr.exia.viewModel.ElementsEditorViewModel
import jdr.exia.viewModel.ElementsTabViewModel

@Composable
fun ElementsView(onDone: DefaultFunction) = Column {
    val tabViewModel = remember { ElementsTabViewModel() }

    Scaffold(
        backgroundColor = blue,
        topBar = {
            HeaderRow {
                Row(horizontalArrangement = Arrangement.SpaceAround, modifier = Modifier.fillMaxWidth()) {
                    tabViewModel.tabs.forEach { tab ->
                        Text(
                            text = tab.typeName,
                            fontWeight = FontWeight.Bold.takeIf { tabViewModel.currentTab == tab },
                            modifier = Modifier.applyIf(
                                condition = tabViewModel.currentTab == tab,
                                mod = { border(bottom = BorderInlined(5.dp, Color.Black)) }
                            ).clickable { tabViewModel.onSelectTab(tab) }.padding(20.dp)
                        )
                    }
                }
            }
        },
        content = { Content(it, tabViewModel.currentTab) },
        bottomBar = {
            FooterRow(
                lazyResult = lazy { Result.Success },
                onDone = onDone
            )
        }
    )
}

@Composable
private fun Content(innerPadding: PaddingValues, currentType: Type) = Box(modifier = Modifier.padding(innerPadding)) {
    val viewModel = remember(currentType) { ElementsEditorViewModel(currentType) }

    Column(modifier = Modifier.fillMaxSize().padding(15.dp)) {
        val headerScrollState = rememberScrollState()

        Box(
            modifier = Modifier.padding(top = 20.dp, end = 20.dp, start = 20.dp)
                .background(Color.White)
                .fillMaxWidth()
                .border(BorderInlined.defaultBorder)
        ) {
            Box(modifier = Modifier.verticalScroll(headerScrollState).fillMaxSize()) {
                Column {
                    ContentListRow(
                        contentText = currentType.typeName,
                        modifier = Modifier.background(Color.White).border(BorderInlined.defaultBorder),
                        buttonBuilders =
                        if (viewModel.blueprintInCreation == null) {
                            listOf(
                                ButtonBuilder(
                                    icon = imageFromIconRes("create_icon"),
                                    onClick = viewModel::startBlueprintCreation
                                )
                            )
                        } else {
                            listOf(
                                ButtonBuilder(
                                    icon = imageFromIconRes("confirm_icon"),
                                    onClick = {
                                        if (viewModel.onSubmitBlueprint()) {
                                            viewModel.cancelBluprintCreation()
                                        } else {
                                            showMessage(
                                                StringLocale[ST_SCENE_ALREADY_EXISTS_OR_INVALID],
                                                messageType = MessageType.WARNING
                                            )
                                        }
                                    }
                                ),
                                ButtonBuilder(
                                    icon = imageFromIconRes("exit_icon"),
                                    onClick = viewModel::cancelBluprintCreation
                                )
                            )
                        }
                    )
                }
            }
        }


        if (viewModel.blueprintInCreation != null) {
            /*EditSceneRow(
                data = sceneInCreation,
                updateData = setSceneInCreation,
                showButtons = false,
                modifier = contentModifier
            )*/
        } else {
            ScrolableContent(viewModel)
        }
    }
}

@Composable
private fun ColumnScope.ScrolableContent(viewModel: ElementsEditorViewModel) {
    val listState = rememberLazyListState()

    val contentModifier = remember {
        Modifier.padding(bottom = 20.dp, end = 20.dp, start = 20.dp)
            .background(Color.White)
            .weight(1f)
            .fillMaxSize()
            .border(BorderInlined.defaultBorder)
    }

    LazyColumn(
        modifier = contentModifier,
        state = listState
    ) {
        items(items = viewModel.blueprints) { blueprint ->
            var editedData by (viewModel.currentEditBlueprint == blueprint).let { isEditing ->
                remember(isEditing) { mutableStateOf(blueprint.takeIf { isEditing }?.toBlueprintData()) }
            }

            ContentListRow(
                content = {
                    editedData.let { data ->
                        if (data == null) {
                            ContentText(blueprint.name)
                        } else {
                            TextField(
                                value = data.name,
                                onValueChange = { editedData = data.copy(name = it) },
                                placeholder = { Text(text = blueprint.name) },
                                colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.White),
                                modifier = Modifier.padding(horizontal = 4.dp).fillMaxWidth()
                            )
                        }
                    }
                },
                buttonBuilders = editedData.getButtons(viewModel, blueprint)
            )
        }
    }
}

private fun Blueprint.BlueprintData?.getButtons(
    viewModel: ElementsEditorViewModel,
    blueprint: Blueprint
) = if (this == null) {
    listOf(
        ButtonBuilder(
            imageFromIconRes("edit_icon"),
            onClick = {
                viewModel.onEditItemSelected(blueprint)
                viewModel.cancelBluprintCreation()
            }
        ),
        ButtonBuilder(
            imageFromIconRes("delete_icon"),
            onClick = { viewModel.onRemoveBlueprint(blueprint) }
        )
    )
} else {
    listOf(
        ButtonBuilder(
            imageFromIconRes("confirm_icon"),
            onClick = {
                when (val result = viewModel.onEditConfirmed(this)) {
                    is Result.Failure -> {
                        result.message.let {
                            if (it != null) {
                                showMessage(it, messageType = MessageType.WARNING)
                            } else {
                                showMessage(StringLocale[ST_UNKNOWN_ERROR], messageType = MessageType.WARNING)
                                viewModel.onEditDone()
                            }
                        }
                    }
                    else -> viewModel.onEditDone()
                }
            }
        ),
        ButtonBuilder(
            imageFromIconRes("exit_icon"),
            onClick = { viewModel.onEditDone() }
        )
    )
}