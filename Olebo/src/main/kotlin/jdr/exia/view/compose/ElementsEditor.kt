@file:Suppress("FunctionName")

package jdr.exia.view.compose

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import jdr.exia.localization.*
import jdr.exia.model.element.Blueprint
import jdr.exia.model.element.Type
import jdr.exia.model.tools.imageFromFile
import jdr.exia.model.tools.imageFromIconRes
import jdr.exia.model.tools.savePathToImage
import jdr.exia.model.utils.Result
import jdr.exia.model.utils.isCharacter
import jdr.exia.view.compose.components.*
import jdr.exia.view.compose.tools.BorderBuilder
import jdr.exia.view.compose.tools.DefaultFunction
import jdr.exia.view.compose.tools.applyIf
import jdr.exia.view.compose.tools.border
import jdr.exia.view.compose.ui.blue
import jdr.exia.view.utils.MessageType
import jdr.exia.view.utils.showMessage
import jdr.exia.viewModel.ElementsEditorViewModel
import jdr.exia.viewModel.ElementsTabViewModel
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import javax.imageio.ImageIO
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

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
                                mod = { border(bottom = BorderBuilder(5.dp, Color.Black)) }
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

private val ColumnScope.contentModifier
    get() = Modifier.padding(bottom = 20.dp, end = 20.dp, start = 20.dp)
        .background(Color.White)
        .weight(1f)
        .fillMaxSize()
        .border(BorderBuilder.defaultBorder)

@Composable
private fun Content(innerPadding: PaddingValues, currentType: Type) = Box(modifier = Modifier.padding(innerPadding)) {
    val viewModel = remember(currentType) { ElementsEditorViewModel(currentType) }

    Column(modifier = Modifier.fillMaxSize().padding(15.dp)) {
        val headerScrollState = rememberScrollState()

        HeaderContent(headerScrollState, currentType, viewModel)

        when {
            viewModel.blueprintInCreation != null -> {
                // Edition on
            }
            viewModel.blueprints.isEmpty() -> {
                Column(modifier = contentModifier, verticalArrangement = Arrangement.Center) {
                    Text(
                        text = StringLocale[STR_NO_ELEMENT],
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        fontWeight = FontWeight.Bold,
                        fontSize = 30.sp
                    )
                    OutlinedButton(
                        onClick = viewModel::startBlueprintCreation,
                        content = { Text(StringLocale[STR_ADD_ELEMENT]) },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
            else -> {
                ScrolableContent(viewModel)
            }
        }
    }
}

@Composable
private fun HeaderContent(
    headerScrollState: ScrollState,
    currentType: Type,
    viewModel: ElementsEditorViewModel
) = Box(
    modifier = Modifier.padding(top = 20.dp, end = 20.dp, start = 20.dp)
        .background(Color.White)
        .fillMaxWidth()
        .border(BorderBuilder.defaultBorder)
) {
    Box(modifier = Modifier.verticalScroll(headerScrollState).fillMaxSize()) {
        Column {
            ContentListRow(
                contentText = currentType.typeName,
                modifier = Modifier.background(Color.White).border(BorderBuilder.defaultBorder),
                buttonBuilders =
                if (viewModel.blueprintInCreation == null) {
                    if (viewModel.blueprints.isNotEmpty()) {
                        if (currentType.type.typeElement != Type.OBJECT) listOf(
                            ContentButtonBuilder(
                                content = StringLocale[STR_HP]
                            ),
                            ContentButtonBuilder(
                                content = StringLocale[STR_MP]
                            )
                        ) else {
                            emptyList()
                        } + listOf(
                            ContentButtonBuilder(
                                content = StringLocale[STR_IMG]
                            ),
                            EmptyContent
                        )
                    } else {
                        emptyList()
                    } + listOf(
                        ImageButtonBuilder(
                            content = imageFromIconRes("create_icon"),
                            onClick = viewModel::startBlueprintCreation
                        )
                    )
                } else {
                    listOf(
                        ImageButtonBuilder(
                            content = imageFromIconRes("confirm_icon"),
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
                        ImageButtonBuilder(
                            content = imageFromIconRes("exit_icon"),
                            onClick = viewModel::cancelBluprintCreation
                        )
                    )
                }
            )
        }
    }
}

@Composable
private fun ColumnScope.ScrolableContent(viewModel: ElementsEditorViewModel) {
    val listState = rememberLazyListState()

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
                                modifier = Modifier.padding(horizontal = 4.dp).fillMaxWidth(),
                                singleLine = true
                            )
                        }
                    }
                },
                buttonBuilders = editedData.getButtons(
                    viewModel = viewModel,
                    blueprint = blueprint,
                    onUpdate = { editedData = it }
                )
            )
        }
    }
}

private fun Blueprint.BlueprintData?.getButtons(
    viewModel: ElementsEditorViewModel,
    blueprint: Blueprint,
    onUpdate: (Blueprint.BlueprintData) -> Unit
) = if (this == null) {
    if (blueprint.isCharacter()) transaction {
        listOf(
            ContentButtonBuilder(content = blueprint.HP),
            ContentButtonBuilder(content = blueprint.MP)
        )
    } else {
        emptyList()
    } + listOf(
        ImageButtonBuilder(
            content = imageFromFile(File(blueprint.sprite))
        ),
        ImageButtonBuilder(
            content = imageFromIconRes("edit_icon"),
            onClick = {
                viewModel.onEditItemSelected(blueprint)
                viewModel.cancelBluprintCreation()
            }
        ),
        ImageButtonBuilder(
            content = imageFromIconRes("delete_icon"),
            onClick = { viewModel.onRemoveBlueprint(blueprint) }
        )
    )
} else {
    if (blueprint.isCharacter()) transaction {
        listOf(
            ComposableContentBuilder(
                content = {
                    IntTextField(
                        value = life,
                        onValueChange = { onUpdate(copy(life = it)) },
                        modifier = Modifier.padding(2.dp).padding(start = 1.dp),
                        colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.White),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    )
                }
            ),
            ComposableContentBuilder(
                content = {
                    IntTextField(
                        value = mana,
                        onValueChange = { onUpdate(copy(mana = it)) },
                        modifier = Modifier.padding(2.dp).padding(start = 1.dp),
                        colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.White),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    )
                }
            )
        )
    } else {
        emptyList()
    } + listOf(
        ImageButtonBuilder(
            content = imageFromFile(File(img.path)),
            onClick = { updateImage(onUpdate) }
        ),
        ImageButtonBuilder(
            content = imageFromIconRes("confirm_icon"),
            onClick = { submitData(viewModel) }
        ),
        ImageButtonBuilder(
            content = imageFromIconRes("exit_icon"),
            onClick = { viewModel.onEditDone() }
        )
    )
}

private fun Blueprint.BlueprintData.submitData(viewModel: ElementsEditorViewModel) {
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

private inline fun Blueprint.BlueprintData.updateImage(crossinline onUpdate: (Blueprint.BlueprintData) -> Unit) {
    transaction {
        val file = JFileChooser().apply {
            this.currentDirectory = File(System.getProperty("user.home"))
            this.addChoosableFileFilter(
                FileNameExtensionFilter(StringLocale[STR_IMG], *ImageIO.getReaderFileSuffixes())
            )
            this.isAcceptAllFileFilterUsed = false
        }

        val result = file.showSaveDialog(null) // TODO : Add parent

        if (result == JFileChooser.APPROVE_OPTION) {
            val selectedFile = file.selectedFile

            if (selectedFile.exists()) {
                onUpdate(copy(img = savePathToImage(selectedFile.absolutePath, "blueprint")))
            }
        }
    }
}